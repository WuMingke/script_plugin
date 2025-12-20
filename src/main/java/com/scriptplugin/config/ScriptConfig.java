package com.scriptplugin.config;

import java.util.Objects;

/**
 * 脚本配置实体类
 */
public class ScriptConfig {
    private String name;
    private String path;
    private String workingDirectory;
    private String arguments;
    private String description;

    public ScriptConfig() {
    }

    public ScriptConfig(String name, String path, String workingDirectory, String arguments, String description) {
        this.name = name;
        this.path = path;
        this.workingDirectory = workingDirectory;
        this.arguments = arguments;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptConfig that = (ScriptConfig) o;
        return Objects.equals(name, that.name) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}

