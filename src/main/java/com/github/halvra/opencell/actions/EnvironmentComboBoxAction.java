package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class EnvironmentComboBoxAction extends ComboBoxAction implements DumbAware {
    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    @NotNull
    protected JComponent createCustomComponent(ComboBoxButton button) {
        NonOpaquePanel panel = new NonOpaquePanel(new BorderLayout());
        Border border = UIUtil.isUnderDefaultMacTheme() ?
                JBUI.Borders.empty(0, 2) : JBUI.Borders.empty(0, 5, 0, 4);

        panel.setBorder(border);
        panel.add(button);
        return panel;
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
        final DefaultActionGroup allActionsGroup = new DefaultActionGroup();
        final Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(button));
        if (project == null) {
            return allActionsGroup;
        }

        final ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        allActionsGroup.addSeparator();
        settings.getEnvironments().forEach(environment -> allActionsGroup.add(selectAction(project, environment)));

        return allActionsGroup;
    }

    protected abstract AnAction selectAction(Project project, Environment environment);
}
