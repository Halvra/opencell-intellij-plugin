package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class EnvironmentComboBoxAction extends ComboBoxAction implements DumbAware {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        try {
            if (!ScriptUtil.isScript(psiFile)) {
                presentation.setEnabledAndVisible(false);
            } else {
                Project project = e.getData(CommonDataKeys.PROJECT);
                presentation.setEnabled(project != null && !project.isDisposed() && project.isOpen());
            }
        } catch (IndexNotReadyException ignored) {
            presentation.setEnabled(false);
        }
    }

    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(@NotNull final Presentation presentation, @NotNull String place) {
        ComboBoxButton myButton = new ComboBoxButton(presentation) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = Math.max(d.width, JBUIScale.scale(75));
                return d;
            }
        };
        return createCustomComponent(myButton);
    }

    @NotNull
    protected JComponent createCustomComponent(ComboBoxButton button) {
        NonOpaquePanel panel = new NonOpaquePanel(new BorderLayout());
        Border border = JBUI.Borders.empty(0, 5, 0, 4);

        panel.setBorder(border);
        panel.add(button);
        return panel;
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(@NotNull JComponent button, @NotNull DataContext context) {
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
