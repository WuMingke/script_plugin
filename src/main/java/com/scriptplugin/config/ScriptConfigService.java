package com.scriptplugin.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本配置服务，用于持久化存储脚本配置
 */
@State(
    name = "ScriptConfigService",
    storages = @Storage("script-runner.xml")
)
@Service
public final class ScriptConfigService implements PersistentStateComponent<ScriptConfigService.State> {
    
    public static class State {
        public List<ScriptConfig> scripts = new ArrayList<>();
    }

    private State myState = new State();

    public static ScriptConfigService getInstance() {
        return ApplicationManager.getApplication().getService(ScriptConfigService.class);
    }

    @Override
    public @Nullable State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        if (myState.scripts == null) {
            myState.scripts = new ArrayList<>();
        }
    }

    public List<ScriptConfig> getScripts() {
        return new ArrayList<>(myState.scripts);
    }

    public void addScript(ScriptConfig script) {
        myState.scripts.add(script);
    }

    public void removeScript(ScriptConfig script) {
        myState.scripts.remove(script);
    }

    public void updateScript(int index, ScriptConfig script) {
        if (index >= 0 && index < myState.scripts.size()) {
            myState.scripts.set(index, script);
        }
    }

    public void setScripts(List<ScriptConfig> scripts) {
        myState.scripts = new ArrayList<>(scripts);
    }
}

