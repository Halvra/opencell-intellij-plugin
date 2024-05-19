package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.DeployScriptToEnvironmentTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;
import org.meveo.api.dto.ScriptInstanceDto;

public class DirectDeployComboBoxAction extends EnvironmentComboBoxAction implements DumbAware {
    @Override
    protected AnAction selectAction(Project project, Environment environment) {
        return new SelectEnvironmentAction(project, environment, (anActionEvent, selectEnvironmentAction) -> {
            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null && ProblemsCollector.getInstance(project).getFileProblemCount(psiFile.getVirtualFile()) == 0 && ScriptUtil.isScript(psiFile)) {
                ScriptInstanceDto scriptInstance = ScriptUtil.getScriptInstanceFromPsiJavaFile((PsiJavaFileImpl) psiFile);
                ProgressManager progressManager = ProgressManager.getInstance();

                progressManager.run(new DeployScriptToEnvironmentTask(project, scriptInstance, environment));
            }
        });
    }

    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
