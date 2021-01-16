package com.github.halvra.opencell.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import org.jetbrains.annotations.NotNull;

public class AutoDeployAction extends CheckboxAction {
    private boolean selected;

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        selected = state;
    }
}
