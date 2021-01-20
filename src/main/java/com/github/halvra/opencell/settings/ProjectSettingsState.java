package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
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
        storages = {@Storage("OpencellPlugin.xml")}
)
@Getter
@Setter
public class ProjectSettingsState implements PersistentStateComponent<ProjectSettingsState> {
    public static final String DEFAULT_SCRIPT_INTERFACE = "org.meveo.service.script.Script";

    private List<Environment> environments = new ArrayList<>();
    private List<String> scriptInterfaces = new ArrayList<>();

    public static ProjectSettingsState getInstance(Project project) {
        if (project == null) {
            return null;
        }

        return ServiceManager.getService(project, ProjectSettingsState.class);
    }

    @Override
    public void initializeComponent() {
        if (this.scriptInterfaces.isEmpty() || !this.scriptInterfaces.contains(DEFAULT_SCRIPT_INTERFACE)) {
            this.scriptInterfaces.add(DEFAULT_SCRIPT_INTERFACE);
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
