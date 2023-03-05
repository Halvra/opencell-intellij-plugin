package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "com.github.halvra.opencell.settings.ProjectSettingsState",
        storages = {@Storage("OpencellPlugin.xml")},
        category = SettingsCategory.TOOLS
)
@Getter
@Setter
public class ProjectSettingsState implements PersistentStateComponent<ProjectSettingsState> {

    private List<Environment> environments = new ArrayList<>();
    private List<String> scriptInterfaces = new ArrayList<>();
    private boolean scriptInterfacesAutoDetected = false;

    public static ProjectSettingsState getInstance(Project project) {
        if (project == null) {
            return null;
        }

        return project.getService(ProjectSettingsState.class);
    }

    @Override
    public void initializeComponent() {
        if (this.scriptInterfaces.isEmpty() || !this.scriptInterfaces.contains(ScriptUtil.DEFAULT_SCRIPT_INTERFACE)) {
            this.scriptInterfaces.add(ScriptUtil.DEFAULT_SCRIPT_INTERFACE);
        }
    }

    @Override
    public @Nullable ProjectSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setPreferredEnvironment(Environment environment) {
        environments.forEach(env -> env.setPreferred(env.equals(environment)));
    }

    @Transient
    public Environment getPreferredEnvironment() {
        return environments.isEmpty() ? null : environments.stream().filter(Environment::isPreferred).findFirst().orElse(environments.get(0));
    }
}
