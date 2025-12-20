import * as vscode from 'vscode';
import { ScriptConfigService, ScriptConfig } from './scriptConfig';
import { ScriptRunner } from './scriptRunner';

/**
 * 激活扩展
 */
export function activate(context: vscode.ExtensionContext) {
    console.log('Script Runner 扩展已激活');

    // 注册"运行脚本"命令
    const runScriptCommand = vscode.commands.registerCommand('scriptRunner.runScript', async (uri?: vscode.Uri) => {
        const scripts = ScriptConfigService.getScripts();

        if (scripts.length === 0) {
            const action = await vscode.window.showInformationMessage(
                '请先配置脚本。是否现在配置？',
                '配置脚本'
            );
            if (action === '配置脚本') {
                vscode.commands.executeCommand('workbench.action.openSettings', 'scriptRunner.scripts');
            }
            return;
        }

        // 显示脚本选择列表
        const items = scripts.map(script => ({
            label: script.name,
            description: script.description || script.path || '',
            detail: script.path || '使用命令模式',
            script: script
        }));

        const selected = await vscode.window.showQuickPick(items, {
            placeHolder: '选择要运行的脚本'
        });

        if (selected) {
            // 获取当前选中的文件
            const activeEditor = vscode.window.activeTextEditor;
            const contextFile = uri || (activeEditor ? activeEditor.document.uri : undefined);
            
            await ScriptRunner.runScript(selected.script, contextFile);
        }
    });

    // 注册"配置脚本"命令
    const configureScriptsCommand = vscode.commands.registerCommand('scriptRunner.configureScripts', async () => {
        await vscode.commands.executeCommand('workbench.action.openSettings', 'scriptRunner.scripts');
    });

    context.subscriptions.push(runScriptCommand, configureScriptsCommand);
}

/**
 * 停用扩展
 */
export function deactivate() {
    console.log('Script Runner 扩展已停用');
}

