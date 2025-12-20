package com.scriptplugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.scriptplugin.config.ScriptConfig;
import com.scriptplugin.config.ScriptConfigService;
import com.scriptplugin.runner.ScriptRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 右键菜单中的运行脚本 ActionGroup
 * 动态显示所有配置的脚本作为子菜单
 */
public class RunScriptAction extends ActionGroup {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        if (e == null) {
            return AnAction.EMPTY_ARRAY;
        }

        Project project = e.getProject();
        if (project == null) {
            return AnAction.EMPTY_ARRAY;
        }

        ScriptConfigService configService = ScriptConfigService.getInstance();
        List<ScriptConfig> scripts = configService.getScripts();

        if (scripts.isEmpty()) {
            // 如果没有配置脚本，显示一个提示项
            return new AnAction[]{new AnAction("请先配置脚本...") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    com.intellij.openapi.ui.Messages.showInfoMessage(
                            project,
                            "请先在 Settings -> Tools -> Script Runner 中配置脚本",
                            "提示"
                    );
                }
            }};
        }

        // 为每个脚本创建一个 Action
        List<AnAction> actions = new ArrayList<>();
        VirtualFile selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        
        for (ScriptConfig script : scripts) {
            actions.add(new AnAction(script.getName(), script.getDescription(), null) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    ScriptRunner runner = new ScriptRunner(project);
                    runner.runScript(script, selectedFile);
                }
            });
        }

        return actions.toArray(new AnAction[0]);
    }
}

