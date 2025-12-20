# Script Runner - VSCode 插件

一个用于在 VSCode 中快速运行本地项目脚本的插件。

## 功能特性

- ✅ 在编辑区右键菜单中快速运行配置的脚本
- ✅ 在 Settings 中配置和管理脚本
- ✅ 支持多种脚本类型（Shell、Python、Node.js、Ruby 等）
- ✅ 支持脚本参数和工作目录配置
- ✅ 支持变量替换（如 `${file}`, `${projectDir}` 等）
- ✅ 在 Terminal 中执行脚本，输出清晰可见

## 使用方法

### 1. 配置脚本

1. 打开 VSCode
2. 进入 `Settings` (macOS: `Cmd + ,`, Windows/Linux: `Ctrl + ,`)
3. 搜索 `Script Runner` 或 `scriptRunner.scripts`
4. 点击 `Edit in settings.json` 编辑配置

配置示例：

```json
{
  "scriptRunner.scripts": [
    {
      "name": "构建项目",
      "path": "${projectDir}/build.sh",
      "workingDirectory": "${projectDir}",
      "arguments": "--release",
      "description": "构建项目"
    },
    {
      "name": "运行测试",
      "path": "${projectDir}/test.sh",
      "description": "运行测试脚本"
    },
    {
      "name": "自定义命令",
      "arguments": "echo 'Hello World'\necho '${file}'",
      "description": "使用命令模式"
    }
  ]
}
```

### 2. 运行脚本

**方法一：右键菜单**
1. 在编辑区任意位置右键点击
2. 选择 `运行脚本...`
3. 从弹出的列表中选择要运行的脚本

**方法二：命令面板**
1. 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
2. 输入 `Script Runner: Run Script`
3. 选择要运行的脚本

**方法三：资源管理器**
1. 在资源管理器中右键点击文件
2. 选择 `运行脚本...`

## 支持的脚本类型

- **Shell 脚本** (`.sh`, `.bash`)
- **Python 脚本** (`.py`)
- **Node.js 脚本** (`.js`, `.mjs`)
- **Ruby 脚本** (`.rb`)
- **其他可执行文件**（需要执行权限）

## 变量替换

在脚本路径、工作目录和参数中可以使用以下变量：

- `${file}` - 当前选中文件的完整路径
- `${fileDir}` - 当前选中文件所在目录
- `${fileName}` - 当前选中文件名（含扩展名）
- `${fileNameWithoutExtension}` - 当前选中文件名（不含扩展名）
- `${projectDir}` - 项目根目录（工作区根目录）

## 配置说明

### ScriptConfig 对象属性

- **name** (必需): 脚本显示名称
- **path** (可选): 脚本文件的完整路径。如果为空，则使用 `arguments` 字段的内容作为命令
- **workingDirectory** (可选): 脚本运行的工作目录。如果为空，则使用当前文件所在目录或项目根目录
- **arguments** (可选): 脚本运行时的参数（支持变量替换）。如果 `path` 为空，此字段的内容将作为脚本命令执行
- **description** (可选): 脚本描述

### 命令模式

如果 `path` 为空，插件会将 `arguments` 字段的内容作为脚本命令执行。这对于快速执行一些简单的命令非常有用。

示例：

```json
{
  "name": "清理构建",
  "arguments": "rm -rf build/\nrm -rf dist/",
  "description": "清理构建目录"
}
```

## 开发

### 环境要求

- Node.js >= 16.0.0
- npm 或 yarn

### 安装依赖

```bash
npm install
```

### 编译

```bash
npm run compile
```

### 打包插件

```bash
npm run package
```

打包后的插件文件为 `.vsix` 格式，位于项目根目录。

### 安装插件

1. 在 VSCode 中按 `F1` 打开命令面板
2. 输入 `Extensions: Install from VSIX...`
3. 选择打包好的 `.vsix` 文件

### 调试

1. 按 `F5` 启动调试
2. 会打开一个新的 VSCode 窗口（Extension Development Host）
3. 在新窗口中测试插件功能

## 与 IntelliJ IDEA 插件的对比

| 功能 | IntelliJ IDEA 插件 | VSCode 插件 |
|------|------------------|------------|
| 右键菜单运行 | ✅ | ✅ |
| Settings 配置 | ✅ | ✅ |
| 多语言支持 | ✅ | ✅ |
| Terminal 集成 | ✅ | ✅ |
| 变量替换 | ✅ | ✅ |
| 多行命令支持 | ✅ | ✅ |
| 工作目录配置 | ✅ | ✅ |
| GUI 配置界面 | ✅ | ❌ (使用 JSON 配置) |

## 技术栈

- TypeScript
- VSCode Extension API
- Node.js

## 许可证

MIT License

