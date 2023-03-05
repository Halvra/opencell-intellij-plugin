package com.github.halvra.opencell.utils;

import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ScriptInstanceDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for Opencell Scripts
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScriptUtil {
    public static final String DEFAULT_SCRIPT_INTERFACE = "org.meveo.service.script.ScriptInterface";

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
        return ReadAction.compute(() -> {
            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                ProjectSettingsState settings = ProjectSettingsState.getInstance(psiFile.getProject());

                return Arrays.stream(psiJavaFile.getClasses()).filter(clazz -> !PsiUtil.isAbstractClass(clazz)).anyMatch(clazz -> {
                    String superClassName = clazz.getSuperClass() != null ? clazz.getSuperClass().getQualifiedName() : "";
                    return StringUtils.isNotBlank(superClassName) && settings.getScriptInterfaces().contains(superClassName);
                });
            }

            return false;
        });
    }

    /**
     * Detect interfaces or abstract classes inherits default script interface for given {@link Project}
     */
    public static List<String> getScriptsInterfaces(Project project) {
        return ReadAction.compute(() -> {
            var projectScope = GlobalSearchScope.allScope(project);
            var psiFacade = JavaPsiFacade.getInstance(project);
            var defaultScriptInterfaceClass = psiFacade.findClass(DEFAULT_SCRIPT_INTERFACE, projectScope);
            var query = ClassInheritorsSearch.search(defaultScriptInterfaceClass, projectScope, true);

            return query.allowParallelProcessing().findAll().stream()
                    .filter(psiClass -> psiClass.isInterface() || psiClass.hasModifier(JvmModifier.ABSTRACT))
                    .map(PsiClass::getQualifiedName)
                    .collect(Collectors.toList());
        });
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
