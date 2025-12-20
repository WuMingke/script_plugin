import * as vscode from 'vscode';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';
import { ScriptConfig } from './scriptConfig';

/**
 * 脚本运行器
 */
export class ScriptRunner {
    /**
     * 运行脚本
     */
    static async runScript(script: ScriptConfig, contextFile?: vscode.Uri): Promise<void> {
        try {
            // 构建命令
            const command = await this.buildCommand(script, contextFile);
            
            // 确定工作目录
            const workingDir = this.getWorkingDirectory(script, contextFile);
            
            // 创建终端并执行命令
            const terminal = vscode.window.createTerminal({
                name: script.name,
                cwd: workingDir
            });
            
            terminal.show();
            terminal.sendText(command);
            
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : String(error);
            vscode.window.showErrorMessage(`运行脚本失败: ${errorMessage}`);
        }
    }

    /**
     * 构建执行命令
     */
    private static async buildCommand(script: ScriptConfig, contextFile?: vscode.Uri): Promise<string> {
        let scriptFile: string;
        let isTemporaryScript = false;
        let tempFile: string | null = null;

        // 如果脚本路径为空，使用参数中的命令创建临时脚本
        if (!script.path || script.path.trim() === '') {
            const commands = script.arguments || '';
            if (commands.trim() === '') {
                throw new Error('脚本路径和命令都为空');
            }

            // 创建临时脚本文件
            const tempDir = os.tmpdir();
            const tempFileName = `script_runner_${Date.now()}.sh`;
            tempFile = path.join(tempDir, tempFileName);
            
            // 写入命令到临时文件
            const scriptContent = `#!/bin/bash\nset -e\n${commands}\n`;
            fs.writeFileSync(tempFile, scriptContent, { mode: 0o755 });
            scriptFile = tempFile;
            isTemporaryScript = true;
        } else {
            scriptFile = this.replaceVariables(script.path, contextFile);
            
            // 检查文件是否存在
            if (!fs.existsSync(scriptFile)) {
                throw new Error(`脚本文件不存在: ${scriptFile}`);
            }
        }

        // 根据文件扩展名确定执行命令
        const fileName = path.basename(scriptFile).toLowerCase();
        let command = '';

        if (fileName.endsWith('.sh') || fileName.endsWith('.bash')) {
            command = `sh '${scriptFile}'`;
        } else if (fileName.endsWith('.py')) {
            command = `python3 '${scriptFile}'`;
        } else if (fileName.endsWith('.js') || fileName.endsWith('.mjs')) {
            command = `node '${scriptFile}'`;
        } else if (fileName.endsWith('.rb')) {
            command = `ruby '${scriptFile}'`;
        } else {
            // 默认尝试直接执行（需要文件有执行权限）
            command = `'${scriptFile}'`;
        }

        // 如果使用已有脚本文件，将参数作为脚本参数传递
        if (!isTemporaryScript && script.arguments) {
            const args = script.arguments.split(/\s+/).filter(arg => arg.trim() !== '');
            for (const arg of args) {
                const replacedArg = this.replaceVariables(arg, contextFile);
                command += ` '${replacedArg}'`;
            }
        }

        // 如果是临时脚本，在命令执行后添加清理命令
        if (isTemporaryScript && tempFile) {
            command += ` && rm '${tempFile}'`;
        }

        return command;
    }

    /**
     * 获取工作目录
     */
    private static getWorkingDirectory(script: ScriptConfig, contextFile?: vscode.Uri): string {
        if (script.workingDirectory && script.workingDirectory.trim() !== '') {
            return this.replaceVariables(script.workingDirectory, contextFile);
        }

        if (contextFile) {
            const fileDir = path.dirname(contextFile.fsPath);
            return fileDir;
        }

        // 使用工作区根目录
        const workspaceFolders = vscode.workspace.workspaceFolders;
        if (workspaceFolders && workspaceFolders.length > 0) {
            return workspaceFolders[0].uri.fsPath;
        }

        // 使用用户主目录作为后备
        return os.homedir();
    }

    /**
     * 替换参数中的变量
     */
    private static replaceVariables(text: string, contextFile?: vscode.Uri): string {
        if (contextFile) {
            const filePath = contextFile.fsPath;
            const fileDir = path.dirname(filePath);
            const fileName = path.basename(filePath);
            const fileNameWithoutExt = path.basename(filePath, path.extname(filePath));

            text = text.replace(/\${file}/g, filePath);
            text = text.replace(/\${fileDir}/g, fileDir);
            text = text.replace(/\${fileName}/g, fileName);
            text = text.replace(/\${fileNameWithoutExtension}/g, fileNameWithoutExt);
        }

        // 替换项目目录变量
        const workspaceFolders = vscode.workspace.workspaceFolders;
        const projectDir = workspaceFolders && workspaceFolders.length > 0
            ? workspaceFolders[0].uri.fsPath
            : '';
        text = text.replace(/\${projectDir}/g, projectDir);

        return text;
    }
}

