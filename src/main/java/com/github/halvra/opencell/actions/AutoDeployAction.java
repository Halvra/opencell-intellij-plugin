package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class AutoDeployAction extends CheckboxAction {
    public static final Key<Boolean> OPENCELL_AUTO_DEPLOY_KEY = Key.create("OPENCELL_AUTO_DEPLOY");
    private boolean selected;

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (e.getProject() == null) {
            return;
        }
        
        e.getPresentation().setEnabledAndVisible(ProjectSettingsState.getInstance(e.getProject()).getPreferredEnvironment() != null);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        selected = state;

        if (e.getProject() != null) {
            e.getProject().putUserData(OPENCELL_AUTO_DEPLOY_KEY, state);
        }
    }
}
