package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.DeployScriptToEnvironmentTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.ui.scale.JBUIScale;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.meveo.api.dto.ScriptInstanceDto;

import javax.swing.*;
import java.awt.*;

public class DirectDeployComboBoxAction extends EnvironmentComboBoxAction implements UpdateInBackground {
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

    @Override
    protected AnAction selectAction(Project project, Environment environment) {
        return new SelectEnvironmentAction(project, environment);
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
