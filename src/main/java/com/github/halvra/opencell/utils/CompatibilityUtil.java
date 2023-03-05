package com.github.halvra.opencell.utils;

import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompatibilityUtil {
    private static final List<String> REQUIRED_DEPENDENCIES = Collections.singletonList("com.opencellsoft:opencell-admin-ejbs");

    public static boolean haveRequiredDependencies(@NotNull Project project) {
        var haveRequiredDependencies = new AtomicBoolean(false);
        var modules = ModuleManager.getInstance(project).getModules();

        Arrays.stream(modules).forEach(module -> ModuleRootManager.getInstance(module).orderEntries().librariesOnly().forEachLibrary(library -> {
            if (library.getName() != null) {
                var libraryName = library.getName().substring(0, library.getName().lastIndexOf(":"));
                if (REQUIRED_DEPENDENCIES.stream().anyMatch(libraryName::contains)) {
                    haveRequiredDependencies.set(true);
                    return false; // Required dependency found stop iteration
                }
            }
            return true; // Continue searching
        }));

        return haveRequiredDependencies.get();
    }
}
