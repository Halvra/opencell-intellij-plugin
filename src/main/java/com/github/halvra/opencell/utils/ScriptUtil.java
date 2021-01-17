package com.github.halvra.opencell.utils;

import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.impl.source.PsiJavaFileImpl;

/**
 * Utility class for Opencell Scripts
 */
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
     * Retrieve Script description from class Javadoc
     *
     * @param source the file content to parse
     */
    private static String getDescription(String source) {
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
