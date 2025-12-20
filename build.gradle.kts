plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.scriptplugin"
version = "1.0.1"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.3")
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf("java", "terminal"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    patchPluginXml {
        sinceBuild.set("243")  // 2024.3
        untilBuild.set("253.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    
    // 禁用 buildSearchableOptions 任务（如果不需要搜索功能）
    buildSearchableOptions {
        enabled = false
    }
}

dependencies {
    // Kotlin stdlib 由 IntelliJ Platform 自动提供，不需要显式声明
}

