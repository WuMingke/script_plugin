# Script Runner Plugin

一个用于在 IntelliJ IDEA 中运行本地项目脚本的插件。

## 功能特性

- ✅ 在编辑区右键菜单中快速运行配置的脚本
- ✅ 在 Settings 中配置和管理脚本
- ✅ 支持多种脚本类型（Shell、Python、Node.js、Ruby 等）
- ✅ 支持脚本参数和工作目录配置
- ✅ 支持变量替换（如 `${file}`, `${projectDir}` 等）

## 使用方法

### 1. 配置脚本

1. 打开 IntelliJ IDEA
2. 进入 `Settings` (macOS: `Preferences`) -> `Tools` -> `Script Runner`
3. 点击 `+` 按钮添加新脚本
4. 填写脚本信息：
   - **名称**: 脚本显示名称
   - **脚本路径**: 脚本文件的完整路径（可点击"浏览"按钮选择）
   - **工作目录**: 脚本运行的工作目录（可选，留空则使用当前文件所在目录或项目根目录）
   - **参数**: 脚本运行时的参数（可选，支持变量替换）
   - **描述**: 脚本描述（可选）

### 2. 运行脚本

1. 在编辑区任意位置右键点击
2. 选择 `运行脚本` -> `运行脚本...`
3. 从弹出的列表中选择要运行的脚本
4. 脚本将在后台运行，输出显示在 Run 工具窗口中

## 支持的脚本类型

- **Shell 脚本** (`.sh`, `.bash`)
- **Python 脚本** (`.py`)
- **Node.js 脚本** (`.js`, `.mjs`)
- **Ruby 脚本** (`.rb`)
- **其他可执行文件**（需要执行权限）

## 变量替换

在脚本参数中可以使用以下变量：

- `${file}` - 当前选中文件的完整路径
- `${fileDir}` - 当前选中文件所在目录
- `${fileName}` - 当前选中文件名（含扩展名）
- `${fileNameWithoutExtension}` - 当前选中文件名（不含扩展名）
- `${projectDir}` - 项目根目录

## 开发

### 构建项目

```bash
./gradlew buildPlugin
```

### 运行插件

```bash
./gradlew runIde
```

### 打包插件

```bash
./gradlew buildPlugin
```

打包后的插件文件位于 `build/distributions/` 目录下。

## 安装

1. 构建插件后，在 IntelliJ IDEA 中：
   - `Settings` -> `Plugins` -> `⚙️` -> `Install Plugin from Disk...`
   - 选择打包好的 `.zip` 文件
   - 重启 IntelliJ IDEA

## 技术栈

- Java 17
- Kotlin 1.9.20
- IntelliJ Platform SDK
- Gradle

## 许可证

MIT License

