# 插件发布指南

## 📋 发布前准备清单

### 1. 更新 vendor 信息（必需）

请修改 `src/main/resources/META-INF/plugin.xml` 中的 vendor 信息：

```xml
<vendor email="your-email@example.com" url="https://your-website.com">Your Name</vendor>
```

**请提供以下信息：**
- 您的邮箱地址
- 您的网站/项目地址（可选）
- 您的名字或组织名称

### 2. 获取 JetBrains Plugin Repository Token（必需）

1. 访问 [JetBrains Plugin Repository](https://plugins.jetbrains.com/)
2. 登录您的账号（如果没有，需要先注册）
3. 进入 [My Plugins](https://plugins.jetbrains.com/author/me)
4. 点击 "Add new plugin" 创建新插件
5. 填写插件基本信息：
   - **Plugin ID**: `com.scriptplugin.script-runner`（已在 plugin.xml 中配置）
   - **Plugin Name**: `Script Runner`
   - **Description**: 插件的简短描述
6. 创建后，在插件页面找到 **"Token"** 或 **"Upload Token"**
7. 复制这个 token

**请提供：**
- 您的 JetBrains 账号邮箱
- 插件上传 Token

### 3. 插件签名（可选但推荐）

为了安全，建议对插件进行签名。需要：

1. 生成证书（如果还没有）：
   ```bash
   keytool -genkey -keyalg RSA -keysize 2048 -validity 3650 \
     -keystore plugin-signing.jks \
     -storepass <your-password> \
     -alias plugin-signing \
     -dname "CN=Your Name, OU=Your Organization, O=Your Company, L=City, ST=State, C=Country"
   ```

2. 导出证书链和私钥：
   ```bash
   # 导出证书链
   keytool -export -alias plugin-signing -file certificate.crt -keystore plugin-signing.jks
   
   # 导出私钥（需要转换为 PKCS8 格式）
   keytool -importkeystore -srckeystore plugin-signing.jks -destkeystore plugin-signing.p12 -deststoretype PKCS12
   openssl pkcs12 -in plugin-signing.p12 -nocerts -nodes -out private_key.pem
   ```

**请提供（如果选择签名）：**
- 证书链内容（CERTIFICATE_CHAIN）
- 私钥内容（PRIVATE_KEY）
- 私钥密码（PRIVATE_KEY_PASSWORD）

## 🚀 发布步骤

### 方式 1：使用环境变量（推荐）

1. 设置环境变量：

```bash
# 必需：发布 Token
export PUBLISH_TOKEN="your-publish-token-here"

# 可选：插件签名（如果选择签名）
export CERTIFICATE_CHAIN="-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----"
export PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----"
export PRIVATE_KEY_PASSWORD="your-password"
```

2. 执行发布：

```bash
./gradlew publishPlugin
```

### 方式 2：使用 gradle.properties（不推荐，安全性较低）

在项目根目录创建或编辑 `gradle.properties`：

```properties
# 发布 Token
PUBLISH_TOKEN=your-publish-token-here

# 插件签名（可选）
CERTIFICATE_CHAIN=-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----
PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----
PRIVATE_KEY_PASSWORD=your-password
```

⚠️ **注意**：不要将包含敏感信息的 `gradle.properties` 提交到 Git！

### 方式 3：手动上传

如果自动发布遇到问题，可以手动上传：

1. 构建插件：
   ```bash
   ./gradlew buildPlugin
   ```

2. 插件文件位置：`build/distributions/script-plugin-1.0.0.zip`

3. 登录 [JetBrains Plugin Repository](https://plugins.jetbrains.com/)
4. 进入您的插件页面
5. 点击 "Upload" 或 "New Version"
6. 上传 `script-plugin-1.0.0.zip` 文件
7. 填写版本说明
8. 提交审核

## 📝 发布后

1. **等待审核**：JetBrains 团队会审核您的插件（通常 1-3 个工作日）
2. **审核通过后**：插件会出现在 [JetBrains Plugin Repository](https://plugins.jetbrains.com/)
3. **用户安装**：用户可以通过 IDE 的插件市场搜索并安装您的插件

## 🔄 更新版本

当需要发布新版本时：

1. 更新 `build.gradle.kts` 中的版本号：
   ```kotlin
   version = "1.0.1"  // 或其他版本号
   ```

2. 更新 `plugin.xml` 中的版本号：
   ```xml
   <version>1.0.1</version>
   ```

3. 更新 `change-notes` 部分，添加新版本的变更说明

4. 执行发布：
   ```bash
   ./gradlew publishPlugin
   ```

## ❓ 常见问题

### Q: 发布失败怎么办？
A: 检查：
- Token 是否正确
- 网络连接是否正常
- 插件 ID 是否已在 JetBrains 平台注册
- 版本号是否已存在

### Q: 需要签名吗？
A: 签名是可选的，但推荐使用。未签名的插件在安装时会有警告。

### Q: 如何测试插件？
A: 使用 `./gradlew runIde` 在沙盒环境中测试插件。

## 📞 需要帮助？

如果您提供以下信息，我可以帮您完成发布：

1. ✅ 您的邮箱地址（用于 vendor 信息）
2. ✅ 您的网站/项目地址（可选）
3. ✅ 您的名字或组织名称
4. ✅ JetBrains Plugin Repository Token
5. ✅ （可选）签名证书信息

提供这些信息后，我可以：
- 更新 plugin.xml 中的 vendor 信息
- 配置发布环境
- 执行发布命令

