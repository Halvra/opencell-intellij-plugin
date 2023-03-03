package com.github.halvra.opencell.listeners;

import com.github.halvra.opencell.utils.CompatibilityUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class OpencellProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        CompatibilityUtil.haveRequiredDependencies(project);
    }
}
