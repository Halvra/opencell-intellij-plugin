package com.github.halvra.opencell.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

public class EscapeAndCopyAction extends AnAction {
    private static final String SUPPORTED_FILE_EXTENSION = "java";

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(LangDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(selectedFile != null && SUPPORTED_FILE_EXTENSION.equalsIgnoreCase(selectedFile.getExtension()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(LangDataKeys.VIRTUAL_FILE);

        if (selectedFile != null) {
            String selectedFileContent = LoadTextUtil.loadText(selectedFile).toString();
            CopyPasteManager.getInstance().setContents(new StringSelection(selectedFileContent));
        }
    }
}
