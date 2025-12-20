# Script Runner Plugin

一个跨平台的脚本运行插件，支持在 **IntelliJ IDEA** 和 **VSCode/Cursor** 中快速运行本地项目脚本。

## 功能特性

- ✅ 在编辑区右键菜单中快速运行配置的脚本
- ✅ 在 Settings 中配置和管理脚本
- ✅ 支持多种脚本类型（Shell、Python、Node.js、Ruby 等）
- ✅ 支持脚本参数和工作目录配置
- ✅ 支持变量替换（如 `${file}`, `${projectDir}` 等）
- ✅ 支持多行命令执行
- ✅ 输出清晰可见（IntelliJ IDEA 在 Run 窗口，VSCode/Cursor 在 Terminal）

## 支持的平台

| 平台 | 状态 | 安装方式 |
|------|------|----------|
| **IntelliJ IDEA** | ✅ 支持 | [从磁盘安装](#intellij-idea-安装) 或 [Marketplace](https://plugins.jetbrains.com/) |
| **VSCode / Cursor** | ✅ 支持 | [Marketplace](https://marketplace.visualstudio.com/items?itemName=wql-script-runner.wql-script-runner) |

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

---

## IntelliJ IDEA 版本

### 安装

#### 方式一：从 Marketplace 安装（推荐）

1. 打开 IntelliJ IDEA
2. 进入 `Settings` (macOS: `Preferences`) -> `Plugins`
3. 搜索 `Script Runner`
4. 点击 `Install` 安装
5. 重启 IntelliJ IDEA

#### 方式二：从磁盘安装

1. 构建插件：
   ```bash
   ./gradlew buildPlugin
   ```
2. 在 IntelliJ IDEA 中：
   - `Settings` -> `Plugins` -> `⚙️` -> `Install Plugin from Disk...`
   - 选择 `build/distributions/script-plugin-*.zip` 文件
   - 重启 IntelliJ IDEA

### 使用方法

#### 1. 配置脚本

1. 打开 IntelliJ IDEA
2. 进入 `Settings` (macOS: `Preferences`) -> `Tools` -> `Script Runner`
3. 点击 `+` 按钮添加新脚本
4. 填写脚本信息：
   - **名称**: 脚本显示名称
   - **脚本路径**: 脚本文件的完整路径（可点击"浏览"按钮选择）
   - **工作目录**: 脚本运行的工作目录（可选，留空则使用当前文件所在目录或项目根目录）
   - **参数**: 脚本运行时的参数（可选，支持变量替换）
   - **描述**: 脚本描述（可选）

#### 2. 运行脚本

1. 在编辑区任意位置右键点击
2. 选择 `运行脚本` -> `运行脚本...`
3. 从弹出的列表中选择要运行的脚本
4. 脚本将在后台运行，输出显示在 Run 工具窗口中

### 开发

```bash
# 构建插件
./gradlew buildPlugin

# 运行插件（调试模式）
./gradlew runIde

# 打包插件
./gradlew buildPlugin
```

打包后的插件文件位于 `build/distributions/` 目录下。

---

## VSCode / Cursor 版本

### 安装

#### 方式一：从 Marketplace 安装（推荐）

1. 打开 VSCode 或 Cursor
2. 按 `Cmd + Shift + X` (macOS) 或 `Ctrl + Shift + X` (Windows/Linux) 打开扩展面板
3. 搜索 `Script Runner (WQL)`
4. 点击 `Install` 安装

#### 方式二：从 VSIX 文件安装

1. 打包插件：
   ```bash
   cd vscode-extension
   npm install
   npm run package
   ```
2. 在 VSCode/Cursor 中：
   - 按 `F1` 打开命令面板
   - 输入 `Extensions: Install from VSIX...`
   - 选择 `wql-script-runner-*.vsix` 文件

### 使用方法

#### 1. 配置脚本

1. 打开 VSCode 或 Cursor
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

#### 2. 运行脚本

**方法一：右键菜单**
1. 在编辑区任意位置右键点击
2. 选择 `运行脚本...`
3. 从弹出的列表中选择要运行的脚本

**方法二：命令面板**
1. 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
2. 输入 `Script Runner: Run Script` 或 `运行脚本...`
3. 选择要运行的脚本

**方法三：资源管理器**
1. 在资源管理器中右键点击文件
2. 选择 `运行脚本...`

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

### 开发

```bash
cd vscode-extension

# 安装依赖
npm install

# 编译
npm run compile

# 打包插件
npm run package

# 调试
# 按 F5 启动调试，会打开一个新的 VSCode 窗口
```

---

## 项目结构

```
script_plugin/
├── src/                    # IntelliJ IDEA 插件源代码
│   └── main/
│       ├── java/          # Java/Kotlin 源代码
│       └── resources/     # 插件资源文件
├── vscode-extension/      # VSCode/Cursor 插件源代码
│   ├── src/              # TypeScript 源代码
│   ├── package.json      # 插件配置
│   └── README.md         # VSCode 插件文档
├── build.gradle.kts      # Gradle 构建配置
└── README.md             # 项目主文档
```

## 功能对比

| 功能 | IntelliJ IDEA 插件 | VSCode/Cursor 插件 |
|------|------------------|------------------|
| 右键菜单运行 | ✅ | ✅ |
| Settings 配置 | ✅ | ✅ |
| 多语言支持 | ✅ | ✅ |
| Terminal 集成 | ✅ (Run 窗口) | ✅ (Terminal) |
| 变量替换 | ✅ | ✅ |
| 多行命令支持 | ✅ | ✅ |
| 工作目录配置 | ✅ | ✅ |
| GUI 配置界面 | ✅ | ❌ (使用 JSON 配置) |

## 技术栈

### IntelliJ IDEA 插件
- Java 17
- Kotlin 1.9.20
- IntelliJ Platform SDK
- Gradle

### VSCode/Cursor 插件
- TypeScript
- VSCode Extension API
- Node.js

## 许可证

MIT License

## 相关链接

- **IntelliJ IDEA 插件**: [JetBrains Marketplace](https://plugins.jetbrains.com/)（待发布）
- **VSCode/Cursor 插件**: [Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=wql-script-runner.wql-script-runner)
- **GitHub 仓库**: [https://github.com/WuMingke/script_plugin](https://github.com/WuMingke/script_plugin)

