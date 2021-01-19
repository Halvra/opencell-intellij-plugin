package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.OpencellBundle;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
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
        return OpencellBundle.message("plugin.name");
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new ProjectSettingsComponent();
        return component.getMainPanel();
    }

    @Override
    public boolean isModified() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        return !component.getEnvironmentTable().getItems().equals(settings.getEnvironments())
                || !((CollectionListModel<String>)component.getScriptInterfacesList().getModel()).getItems().equals(settings.getScriptInterfaces());
    }

    @Override
    public void apply() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        settings.setEnvironments(new ArrayList<>(component.getEnvironmentTable().getItems()));

        CollectionListModel<String> scriptInterfacesModel = (CollectionListModel<String>) component.getScriptInterfacesList().getModel();
        settings.setScriptInterfaces(new ArrayList<>(scriptInterfacesModel.getItems()));
    }

    @Override
    public void reset() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        component.getEnvironmentTable().getListTableModel().setItems(new ArrayList<>(settings.getEnvironments()));

        CollectionListModel<String> scriptInterfacesModel = (CollectionListModel<String>) component.getScriptInterfacesList().getModel();
        scriptInterfacesModel.replaceAll(new ArrayList<>(settings.getScriptInterfaces()));
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

}
