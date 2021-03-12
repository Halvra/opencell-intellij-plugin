package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.DeployScriptToEnvironmentTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DirectDeployComboBoxAction extends ComboBoxAction implements DumbAware {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (!ScriptUtil.isScript(psiFile)) {
            presentation.setEnabledAndVisible(false);
        } else {
            Project project = e.getData(CommonDataKeys.PROJECT);
            presentation.setEnabled(project != null && !project.isDisposed() && project.isOpen());
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
        settings.getEnvironments().forEach(environment -> allActionsGroup.add(new SelectEnvironmentAction(project, environment)));

        return allActionsGroup;
    }

    private static final class SelectEnvironmentAction extends AnAction {
        private final Project project;
        private final Environment environment;

        SelectEnvironmentAction(final Project project, final Environment environment) {
            this.project = project;
            this.environment = environment;

            String name = environment.getName();
            Presentation presentation = getTemplatePresentation();
            presentation.setText(name, false);
            presentation.setDescription(OpencellBundle.message("select.0", name));

            presentation.setIcon(Icons.OPENCELL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null && ProblemsCollector.getInstance(project).getFileProblemCount(psiFile.getVirtualFile()) == 0 && ScriptUtil.isScript(psiFile)) {
                ScriptInstanceDto scriptInstance = ScriptUtil.getScriptInstanceFromPsiJavaFile((PsiJavaFileImpl) psiFile);

                if (environment != null) {
                    ProgressManager progressManager = ProgressManager.getInstance();
                    progressManager.run(new DeployScriptToEnvironmentTask(project, scriptInstance, environment));
                }
            }
        }
    }
}
