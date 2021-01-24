package com.github.halvra.opencell.utils;

import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Utility class for Opencell Scripts
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScriptUtil {
    /**
     * Retrieve a {@link ScriptInstanceDto} based on provided {@link PsiJavaFileImpl}
     *
     * @param psiJavaFile the file to transform to {@link ScriptInstanceDto}
     */
    public static ScriptInstanceDto getScriptInstanceFromPsiJavaFile(PsiJavaFileImpl psiJavaFile) {
        ScriptInstanceDto scriptInstanceDto = new ScriptInstanceDto();
        scriptInstanceDto.setCode(psiJavaFile.getPackageName() + "." + FileUtil.getNameWithoutExtension(psiJavaFile.getName()));
        scriptInstanceDto.setDescription(getDescription(psiJavaFile.getText()));
        scriptInstanceDto.setScript(psiJavaFile.getText());

        return scriptInstanceDto;
    }

    /**
     * Determine if given {@link PsiFile} is an Opencell Script
     */
    public static boolean isScript(PsiFile psiFile) {
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            ProjectSettingsState settings = ProjectSettingsState.getInstance(psiFile.getProject());

            return Arrays.stream(psiJavaFile.getClasses()).anyMatch(clazz -> {
                String superClassName = clazz.getSuperClass() != null ? clazz.getSuperClass().getQualifiedName() : "";
                return StringUtils.isNotBlank(superClassName) && settings.getScriptInterfaces().contains(superClassName);
            });
        }

        return false;
    }

    /**
     * Retrieve first line of class Javadoc
     *
     * @param source the file content to parse
     */
    private static String getDescription(String source) {
        int i = source.indexOf("/**");
        int j = source.indexOf("public class") - 2;

        if (i > 0 && i < j) {
            String javadoc = source.substring(i + 4, j);
            javadoc = javadoc.substring(0, javadoc.indexOf("\n"));


            if (javadoc.length() >= 255) {
                javadoc = javadoc.substring(0, 252).concat("...");
            }

            javadoc = javadoc.trim();
            javadoc = javadoc.replaceAll("[\\r\\n]", "");
            javadoc = javadoc.replaceAll("\\s\\*|\\*\\s", "");
            javadoc = javadoc.replace("\\*/", "");
            javadoc = javadoc.replace("/", "");

            return javadoc;
        }

        return "";
    }
}
