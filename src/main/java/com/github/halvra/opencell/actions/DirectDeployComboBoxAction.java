package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.DeployScriptToEnvironmentTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.meveo.api.dto.ScriptInstanceDto;

public class DirectDeployComboBoxAction extends EnvironmentComboBoxAction implements UpdateInBackground {

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
