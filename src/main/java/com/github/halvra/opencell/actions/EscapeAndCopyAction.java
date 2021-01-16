package com.github.halvra.opencell.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.halvra.opencell.OpencellNotifier;
import com.github.halvra.opencell.dto.ScriptInstance;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

public class EscapeAndCopyAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(EscapeAndCopyAction.class);

    private static final String SCRIPT = "Script";

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        // File is a Java class
        if (psiFile instanceof PsiJavaFileImpl) {
            PsiJavaFileImpl javaFile = (PsiJavaFileImpl) psiFile;

            // First iterate over referenced classes and verify if extends list contain 'Script'
            boolean isScript = Arrays.stream(javaFile.getClasses()).anyMatch(clazz -> {
                return clazz.getExtendsList() != null && Arrays.stream(clazz.getExtendsList().getReferencedTypes()).anyMatch(ref -> {
                    return ref.getName().equalsIgnoreCase(SCRIPT);
                });
            });

            e.getPresentation().setEnabledAndVisible(isScript);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Project project = e.getProject();

        if (psiFile instanceof PsiJavaFileImpl) {
            PsiJavaFileImpl javaFile = (PsiJavaFileImpl) psiFile;

            ObjectMapper objectMapper = new ObjectMapper();

            ScriptInstance dto = new ScriptInstance();
            dto.setCode(javaFile.getPackageName() + "." + FileUtil.getNameWithoutExtension(javaFile.getName()));
            dto.setDescription(getDescription(psiFile.getText()));
            dto.setScript(psiFile.getText());

            try {
                CopyPasteManager.getInstance().setContents(new StringSelection(objectMapper.writeValueAsString(dto)));
                OpencellNotifier.notifyInformation(project, "Script JSON copied to clipboard");
            } catch (JsonProcessingException ex) {
                LOGGER.error("Error occurred while serializing ScriptInstance to String", ex);
                OpencellNotifier.notifyError(project, "File could not be parsed");
            }
        }
    }

    private String getDescription(String source) {
        int i = source.indexOf("/**");
        int j = source.indexOf("public class") - 2;

        if (i > 0 && i < j) {
            String javadoc = source.substring(i + 3, j);

            if (javadoc.length() >= 255)
                javadoc = javadoc.substring(0, 200).concat(".... ");

            javadoc = javadoc.trim();
            javadoc = javadoc.replaceAll("[\\r\\n]", "");
            javadoc = javadoc.replaceAll("\\s\\*|\\*\\s", "");
            javadoc = javadoc.replaceAll("\\*/", "");
            javadoc = javadoc.replace("/", "");

            return javadoc;
        }

        return "";
    }
}
