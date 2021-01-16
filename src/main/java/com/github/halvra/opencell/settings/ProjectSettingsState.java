package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "com.github.halvra.opencell.settings.ProjectSettingsState",
        storages = {@Storage("OpencellPlugin.xml")}
)
public class ProjectSettingsState implements PersistentStateComponent<ProjectSettingsState> {
    List<Environment> environments = new ArrayList<>();

    public ProjectSettingsState() {
    }

    public static ProjectSettingsState getInstance(Project project) {
        if (project == null) {
            return null;
        }

        return ServiceManager.getService(project, ProjectSettingsState.class);
    }

    @Override
    public @Nullable ProjectSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setPreferredEnvironment(Environment environment) {
        environments.forEach(env -> env.setPreferred(env.equals(environment)));
    }

    public Environment getPreferredEnvironment() {
        return environments.isEmpty() ? null : environments.stream().filter(Environment::isPreferred).findFirst().orElse(environments.get(0));
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }
}
