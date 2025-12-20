package com.scriptplugin.runner;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.scriptplugin.config.ScriptConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * 脚本运行器
 */
public class ScriptRunner {
    private final Project project;

    public ScriptRunner(@NotNull Project project) {
        this.project = project;
    }

    /**
     * 运行脚本
     *
     * @param script 脚本配置
     * @param contextFile 上下文文件（当前选中的文件，可选）
     */
    public void runScript(@NotNull ScriptConfig script, @Nullable VirtualFile contextFile) {
        File[] tempFileRef = new File[1];
        File tempScriptFile = null;
        
        try {
            // 构建命令
            String command = buildCommand(script, contextFile, tempFileRef);
            tempScriptFile = tempFileRef[0];
            
            // 确定工作目录
            String workingDir = script.getWorkingDirectory();
            if (workingDir == null || workingDir.trim().isEmpty()) {
                if (contextFile != null && contextFile.getParent() != null) {
                    workingDir = contextFile.getParent().getPath();
                } else {
                    workingDir = project.getBasePath();
                }
            }
            
            // 获取 Terminal 工具窗口
            ToolWindow terminalWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
            if (terminalWindow == null) {
                Messages.showErrorDialog(project, "无法找到 Terminal 工具窗口", "错误");
                return;
            }
            
            // 激活 Terminal 窗口
            terminalWindow.activate(null);
            
            // 获取 TerminalView 并创建新的终端标签页
            TerminalView terminalView = TerminalView.getInstance(project);
            
            final File finalTempFile = tempScriptFile;
            final String finalWorkingDir = workingDir;
            final String finalCommand = command;
            
            // 创建新的终端标签页
            ShellTerminalWidget widget = terminalView.createLocalShellWidget(finalWorkingDir, script.getName());
            
            try {
                // 直接执行脚本命令
                widget.executeCommand(finalCommand);
                
                // 如果是临时脚本，在命令执行后添加清理命令
                if (finalTempFile != null) {
                    String cleanupCommand = "rm '" + finalTempFile.getAbsolutePath() + "'";
                    widget.executeCommand(cleanupCommand);
                }
            } catch (IOException e) {
                Messages.showErrorDialog(
                        project,
                        "执行命令失败: " + e.getMessage(),
                        "错误"
                );
                // 清理临时文件
                if (finalTempFile != null) {
                    try {
                        Files.deleteIfExists(finalTempFile.toPath());
                    } catch (IOException ignored) {}
                }
            }
            
        } catch (RuntimeException e) {
            Messages.showErrorDialog(
                    project,
                    "运行脚本失败: " + e.getMessage(),
                    "错误"
            );
            // 清理临时文件
            if (tempScriptFile != null) {
                try {
                    Files.deleteIfExists(tempScriptFile.toPath());
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * 构建执行命令
     */
    private String buildCommand(@NotNull ScriptConfig script, @Nullable VirtualFile contextFile, File[] tempFileRef) {
        File scriptFile;
        boolean isTemporaryScript = false;
        
        // 如果脚本路径为空，使用参数中的命令创建临时脚本
        if (script.getPath() == null || script.getPath().trim().isEmpty()) {
            String commands = script.getArguments();
            if (commands == null || commands.trim().isEmpty()) {
                throw new RuntimeException("脚本路径和命令都为空");
            }
            
            try {
                // 创建临时脚本文件
                scriptFile = File.createTempFile("script_runner_", ".sh");
                scriptFile.setExecutable(true);
                isTemporaryScript = true;
                tempFileRef[0] = scriptFile;
                
                // 写入命令到临时文件
                try (FileWriter writer = new FileWriter(scriptFile, StandardCharsets.UTF_8)) {
                    writer.write("#!/bin/bash\n");
                    writer.write("set -e\n"); // 遇到错误立即退出
                    writer.write(commands);
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException("创建临时脚本文件失败: " + e.getMessage(), e);
            }
        } else {
            scriptFile = new File(script.getPath());
            if (!scriptFile.exists()) {
                throw new RuntimeException("脚本文件不存在: " + script.getPath());
            }
        }

        // 根据文件扩展名确定执行命令
        String fileName = scriptFile.getName().toLowerCase();
        StringBuilder commandBuilder = new StringBuilder();

        if (fileName.endsWith(".sh") || fileName.endsWith(".bash")) {
            // Shell 脚本 - 使用 sh 命令执行
            commandBuilder.append("sh '").append(scriptFile.getAbsolutePath()).append("'");
        } else if (fileName.endsWith(".py")) {
            // Python 脚本
            commandBuilder.append("python3 '").append(scriptFile.getAbsolutePath()).append("'");
        } else if (fileName.endsWith(".js") || fileName.endsWith(".mjs")) {
            // Node.js 脚本
            commandBuilder.append("node '").append(scriptFile.getAbsolutePath()).append("'");
        } else if (fileName.endsWith(".rb")) {
            // Ruby 脚本
            commandBuilder.append("ruby '").append(scriptFile.getAbsolutePath()).append("'");
        } else {
            // 默认尝试直接执行（需要文件有执行权限）
            commandBuilder.append("'").append(scriptFile.getAbsolutePath()).append("'");
        }

        // 如果使用临时脚本，不需要添加参数（命令已在脚本中）
        // 如果使用已有脚本文件，将参数作为脚本参数传递
        if (!isTemporaryScript && script.getArguments() != null && !script.getArguments().trim().isEmpty()) {
            String[] args = script.getArguments().split("\\s+");
            for (String arg : args) {
                // 替换可能的变量
                arg = replaceVariables(arg, contextFile);
                commandBuilder.append(" '").append(arg).append("'");
            }
        }

        return commandBuilder.toString();
    }

    /**
     * 替换参数中的变量
     */
    private String replaceVariables(String arg, @Nullable VirtualFile contextFile) {
        if (contextFile != null) {
            arg = arg.replace("${file}", contextFile.getPath());
            arg = arg.replace("${fileDir}", contextFile.getParent() != null ? contextFile.getParent().getPath() : "");
            arg = arg.replace("${fileName}", contextFile.getName());
            arg = arg.replace("${fileNameWithoutExtension}", contextFile.getNameWithoutExtension());
        }
        arg = arg.replace("${projectDir}", project.getBasePath() != null ? project.getBasePath() : "");
        return arg;
    }
}

