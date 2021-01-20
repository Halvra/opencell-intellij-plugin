package com.github.halvra.opencell.tasks;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeployScriptToEnvironmentTask extends Task.Backgroundable {
    private final ScriptInstanceDto scriptInstance;
    private final Environment environment;

    public DeployScriptToEnvironmentTask(@Nullable Project project, @NotNull ScriptInstanceDto scriptInstance, @NotNull Environment environment) {
        super(project, OpencellBundle.message("tasks.deployScript.title"));

        this.scriptInstance = scriptInstance;
        this.environment = environment;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        indicator.setText(OpencellBundle.message("tasks.deployScript.progress", scriptInstance.getCode(), environment.getName()));

        // TODO: Deploy script to environment
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
