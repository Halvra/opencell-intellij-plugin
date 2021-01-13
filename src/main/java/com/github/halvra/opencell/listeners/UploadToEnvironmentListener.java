package com.github.halvra.opencell.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import org.jetbrains.annotations.NotNull;

public class UploadToEnvironmentListener implements FileDocumentManagerListener {
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
    }
}
