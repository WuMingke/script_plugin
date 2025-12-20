import * as vscode from 'vscode';

/**
 * 脚本配置实体类
 */
export interface ScriptConfig {
    name: string;
    path?: string;
    workingDirectory?: string;
    arguments?: string;
    description?: string;
}

/**
 * 脚本配置服务
 */
export class ScriptConfigService {
    private static readonly CONFIG_KEY = 'scriptRunner.scripts';

    /**
     * 获取所有配置的脚本
     */
    static getScripts(): ScriptConfig[] {
        const config = vscode.workspace.getConfiguration();
        return config.get<ScriptConfig[]>(this.CONFIG_KEY, []);
    }

    /**
     * 保存脚本列表
     */
    static async saveScripts(scripts: ScriptConfig[]): Promise<void> {
        const config = vscode.workspace.getConfiguration();
        await config.update(this.CONFIG_KEY, scripts, vscode.ConfigurationTarget.Global);
    }

    /**
     * 添加脚本
     */
    static async addScript(script: ScriptConfig): Promise<void> {
        const scripts = this.getScripts();
        scripts.push(script);
        await this.saveScripts(scripts);
    }

    /**
     * 删除脚本
     */
    static async removeScript(index: number): Promise<void> {
        const scripts = this.getScripts();
        if (index >= 0 && index < scripts.length) {
            scripts.splice(index, 1);
            await this.saveScripts(scripts);
        }
    }

    /**
     * 更新脚本
     */
    static async updateScript(index: number, script: ScriptConfig): Promise<void> {
        const scripts = this.getScripts();
        if (index >= 0 && index < scripts.length) {
            scripts[index] = script;
            await this.saveScripts(scripts);
        }
    }
}

