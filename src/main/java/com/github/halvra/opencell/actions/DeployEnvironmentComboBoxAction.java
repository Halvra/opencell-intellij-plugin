package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.ProjectSettingsConfigurable;
import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SizedIcon;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DeployEnvironmentComboBoxAction extends ComboBoxAction implements DumbAware {
    private static final String BUTTON_MODE = "ButtonMode";

    public static final Icon CHECKED_ICON = JBUIScale.scaleIcon(new SizedIcon(AllIcons.Actions.Checked, 16, 16));
    public static final Icon CHECKED_SELECTED_ICON = JBUIScale.scaleIcon(new SizedIcon(AllIcons.Actions.Checked_selected, 16, 16));

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getData(CommonDataKeys.PROJECT);
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        try {
            if (project == null || project.isDisposed() || !project.isOpen()) {
                updatePresentation(null, null, presentation);
                presentation.setEnabled(false);
            } else {
                updatePresentation(settings.getPreferredEnvironment(), project, presentation);
                presentation.setEnabled(true);
            }
        } catch (IndexNotReadyException e1) {
            presentation.setEnabled(false);
        }
    }

    private static void updatePresentation(@Nullable Environment environment,
                                           @Nullable Project project,
                                           @NotNull Presentation presentation) {
        presentation.putClientProperty(BUTTON_MODE, null);
        if (project != null && environment != null) {
            presentation.setIcon(Icons.OPENCELL);
            presentation.setText(environment.getName());
        } else {
            presentation.putClientProperty(BUTTON_MODE, Boolean.TRUE);
            presentation.setText(OpencellBundle.message("toolbar.environment.add"));
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

            @Override
            protected void doShiftClick() {
                DataContext context = DataManager.getInstance().getDataContext(this);
                final Project project = CommonDataKeys.PROJECT.getData(context);
                ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectSettingsConfigurable.class);

                super.doShiftClick();
            }

            @Override
            protected void fireActionPerformed(ActionEvent event) {
                DataContext context = DataManager.getInstance().getDataContext(this);
                final Project project = CommonDataKeys.PROJECT.getData(context);
                if (Boolean.TRUE.equals(presentation.getClientProperty(BUTTON_MODE))) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectSettingsConfigurable.class);
                    return;
                }

                super.fireActionPerformed(event);
            }

            @Override
            protected boolean isArrowVisible(@NotNull Presentation presentation) {
                return !Boolean.TRUE.equals(presentation.getClientProperty(BUTTON_MODE));
            }
        };
        NonOpaquePanel panel = new NonOpaquePanel(new BorderLayout());
        Border border = UIUtil.isUnderDefaultMacTheme() ?
                JBUI.Borders.empty(0, 2) : JBUI.Borders.empty(0, 5, 0, 4);

        panel.setBorder(border);
        panel.add(myButton);
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
        settings.getEnvironments().forEach(environment -> allActionsGroup.add(new SelectEnvironmentAction(project, environment, environment.isPreferred())));

        return allActionsGroup;
    }

    private static final class SelectEnvironmentAction extends AnAction {
        private final Project project;
        private final Environment environment;

        SelectEnvironmentAction(final Project project, final Environment environment, boolean selected) {
            this.project = project;
            this.environment = environment;

            String name = environment.getName();
            Presentation presentation = getTemplatePresentation();
            presentation.setText(name, false);
            presentation.setDescription(OpencellBundle.message("select.0", name));

            presentation.setIcon(selected ? CHECKED_ICON : Icons.OPENCELL);
            presentation.setSelectedIcon(selected ? CHECKED_SELECTED_ICON : Icons.OPENCELL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            ProjectSettingsState settings = ProjectSettingsState.getInstance(project);
            settings.setPreferredEnvironment(environment);

            updatePresentation(environment,
                    project,
                    e.getPresentation());
        }
    }
}
