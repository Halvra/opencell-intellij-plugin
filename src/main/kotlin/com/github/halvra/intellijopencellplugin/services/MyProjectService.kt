package com.github.halvra.intellijopencellplugin.services

import com.intellij.openapi.project.Project
import com.github.halvra.intellijopencellplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
