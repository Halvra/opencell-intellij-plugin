package com.github.halvra.opencell.tasks;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.OpencellNotifier;
import com.github.halvra.opencell.services.OpencellApiService;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

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

        ScriptInstanceReponseDto response = OpencellApiService.getInstance(environment).createOrUpdateScript(scriptInstance);
        if (response == null || response.getActionStatus().getStatus() != ActionStatusEnum.SUCCESS) {
            String errorMessage;
            if (response == null) {
                errorMessage = OpencellBundle.message("tasks.deployScript.unknownError", environment.getName());
            } else {
                errorMessage = OpencellBundle.message("tasks.deployScript.error", environment.getName(), response.getActionStatus().getMessage());
            }

            OpencellNotifier.notifyError(myProject, errorMessage);
        }
    }
}
