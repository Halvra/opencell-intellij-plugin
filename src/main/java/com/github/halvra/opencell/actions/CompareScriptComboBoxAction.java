package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.OpencellNotifier;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.tasks.CompareScriptTask;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class CompareScriptComboBoxAction extends EnvironmentComboBoxAction implements UpdateInBackground {
    @Override
    protected AnAction selectAction(Project project, Environment environment) {
        return new SelectEnvironmentAction(project, environment, (anActionEvent, selectEnvironmentAction) -> {
            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            if (psiFile instanceof PsiJavaFile
                    && ProblemsCollector.getInstance(project).getFileProblemCount(psiFile.getVirtualFile()) == 0
                    && ScriptUtil.isScript(psiFile)) {
                var scriptInstanceCode = ((PsiJavaFile) psiFile).getPackageName() + "." + FileUtil.getNameWithoutExtension(psiFile.getName());

                try {
                    var environmentScriptInstance = ProgressManager.getInstance().run(new CompareScriptTask(project, scriptInstanceCode, environment));
                    if (environmentScriptInstance != null) {
                        var diffRequestChain = getDiffRequestChain(psiFile, environmentScriptInstance.getScriptInstance().getScript(), environment, project);
                        DiffManager.getInstance().showDiff(project, diffRequestChain, DiffDialogHints.DEFAULT);
                    }
                } catch (Exception ex) {
                    OpencellNotifier.notifyError(project, OpencellBundle.message("tasks.compareScript.error", environment.getName(), ex.getMessage()));
                }
            }
        });
    }

    private DiffRequestChain getDiffRequestChain(PsiFile localScriptFile, String environmentScriptContent, Environment environment, Project project) {
        DiffContent localScript = DiffContentFactory.getInstance().create(project, localScriptFile.getVirtualFile());
        DiffContent environmentScript = DiffContentFactory.getInstance().create(environmentScriptContent, JavaClassFileType.INSTANCE);

        MutableDiffRequestChain chain = new MutableDiffRequestChain(localScript, environmentScript);
        chain.setWindowTitle(OpencellBundle.message("diff.script.vs.environment.dialog.title"));
        chain.setTitle1(OpencellBundle.message("diff.script.vs.environment.content.title"));
        chain.setTitle2(environment.getName());

        return chain;
    }
}
