package com.github.halvra.opencell.listeners;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class UploadToEnvironmentListener implements DocumentListener {
    private final Project project;

    public UploadToEnvironmentListener(Project project) {
        this.project = project;
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // TODO: auto upload to selected Opencell environment when document changed
        // Verify if file contain errors before send
    }
}
