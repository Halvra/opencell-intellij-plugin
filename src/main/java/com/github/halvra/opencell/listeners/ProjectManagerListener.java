package com.github.halvra.opencell.listeners;

import com.github.halvra.opencell.services.ProjectService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ProjectManagerListener implements com.intellij.openapi.project.ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        project.getService(ProjectService.class);
    }


}
