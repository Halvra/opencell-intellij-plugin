package com.github.halvra.opencell.listeners;

import com.github.halvra.opencell.actions.AutoDeployAction;
import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.DeployScriptToEnvironmentTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;

public class UploadToEnvironmentListener implements FileDocumentManagerListener {

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null) {
            return;
        }

        Project project = ProjectLocator.getInstance().guessProjectForFile(file);
        if (project == null) {
            return;
        }

        Boolean autoDeploy = project.getUserData(AutoDeployAction.OPENCELL_AUTO_DEPLOY_KEY);
        if (autoDeploy == null || !autoDeploy) {
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (ProblemsCollector.getInstance(project).getFileProblemCount(file) == 0 && ScriptUtil.isScript(psiFile)) {
            ScriptInstanceDto scriptInstance = ScriptUtil.getScriptInstanceFromPsiJavaFile((PsiJavaFileImpl) psiFile);
            Environment environment = ProjectSettingsState.getInstance(project).getPreferredEnvironment();

            if (environment != null) {
                ProgressManager progressManager = ProgressManager.getInstance();
                progressManager.run(new DeployScriptToEnvironmentTask(project, scriptInstance, environment));
            }
        }
    }
}
