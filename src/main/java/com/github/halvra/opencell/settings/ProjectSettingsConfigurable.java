package com.github.halvra.opencell.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class ProjectSettingsConfigurable implements Configurable {
    private final Project project;
    private ProjectSettingsComponent component;

    public ProjectSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "Opencell Plugin";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new ProjectSettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        return !component.getEnvironmentTable().getItems().equals(settings.getEnvironments());
    }

    @Override
    public void apply() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);
        settings.setEnvironments(new ArrayList<>(component.getEnvironmentTable().getItems()));
    }

    @Override
    public void reset() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);
        component.getEnvironmentTable().getListTableModel().setItems(new ArrayList<>(settings.getEnvironments()));
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

}
