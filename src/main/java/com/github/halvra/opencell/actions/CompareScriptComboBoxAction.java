package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
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
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

public class CompareScriptComboBoxAction extends EnvironmentComboBoxAction implements UpdateInBackground {
    private static final Logger LOGGER = Logger.getInstance(CompareScriptComboBoxAction.class);

    @Override
    protected AnAction selectAction(Project project, Environment environment) {
        return new CompareScriptComboBoxAction.SelectEnvironmentAction(project, environment);
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
            if (psiFile instanceof PsiJavaFile && ProblemsCollector.getInstance(project).getFileProblemCount(psiFile.getVirtualFile()) == 0 && ScriptUtil.isScript(psiFile)) {
                if (environment != null) {
                    var scriptInstanceCode = ((PsiJavaFile) psiFile).getPackageName() + "." + FileUtil.getNameWithoutExtension(psiFile.getName());

                    try {
                        var environmentScriptInstance = ProgressManager.getInstance().run(new CompareScriptTask(project, scriptInstanceCode, environment));
                        if (environmentScriptInstance != null) {
                            var diffRequestChain = getDiffRequestChain(psiFile, environmentScriptInstance.getScriptInstance().getScript(), environment);
                            DiffManager.getInstance().showDiff(project, diffRequestChain, DiffDialogHints.DEFAULT);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        private DiffRequestChain getDiffRequestChain(PsiFile localScriptFile, String environmentScriptContent, Environment environment) {
            DiffContent localScript = DiffContentFactory.getInstance().create(project, localScriptFile.getVirtualFile());
            DiffContent environmentScript = DiffContentFactory.getInstance().create(environmentScriptContent, JavaClassFileType.INSTANCE);

            MutableDiffRequestChain chain = new MutableDiffRequestChain(localScript, environmentScript);
            chain.setWindowTitle(OpencellBundle.message("diff.script.vs.environment.dialog.title"));
            chain.setTitle1(OpencellBundle.message("diff.script.vs.environment.content.title"));
            chain.setTitle2(environment.getName());

            return chain;
        }
    }
}
