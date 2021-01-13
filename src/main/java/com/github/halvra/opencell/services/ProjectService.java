package com.github.halvra.opencell.services;

import com.github.halvra.opencell.OpencellBundle;
import com.intellij.openapi.project.Project;

public class ProjectService {
    private Project project;

    public ProjectService(Project project) {
        this.project = project;
        System.out.println(OpencellBundle.message("projectService", project.getName()));
    }
}
