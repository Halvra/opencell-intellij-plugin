package com.github.halvra.opencell.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.OpencellNotifier;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;
import org.meveo.api.dto.ScriptInstanceDto;

import java.awt.datatransfer.StringSelection;

public class GenerateScriptBodyAction extends AnAction implements UpdateInBackground {
    private static final Logger LOGGER = Logger.getInstance(GenerateScriptBodyAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(ScriptUtil.isScript(psiFile));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();

        if (psiFile instanceof PsiJavaFileImpl) {
            PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiFile;
            ScriptInstanceDto scriptInstanceDto = ScriptUtil.getScriptInstanceFromPsiJavaFile(psiJavaFile);

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                CopyPasteManager.getInstance().setContents(new StringSelection(objectMapper.writeValueAsString(scriptInstanceDto)));
                OpencellNotifier.notifyInformation(project, OpencellBundle.message("action.generateScript.success"));
            } catch (JsonProcessingException ex) {
                LOGGER.error("Error occurred while serializing ScriptInstance to String", ex);
                OpencellNotifier.notifyError(project, OpencellBundle.message("json.parse.error"));
            }
        }
    }
}
