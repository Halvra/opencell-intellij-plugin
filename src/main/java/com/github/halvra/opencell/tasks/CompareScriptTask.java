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
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;

import java.io.IOException;

public class CompareScriptTask extends Task.WithResult<GetScriptInstanceResponseDto, Exception> {
    private final String scriptInstanceCode;
    private final Environment environment;

    public CompareScriptTask(@Nullable Project project, @NotNull String scriptInstanceCode, @NotNull Environment environment) {
        super(project, OpencellBundle.message("tasks.compareScript.title"), true);

        this.scriptInstanceCode = scriptInstanceCode;
        this.environment = environment;
    }

    @Override
    protected GetScriptInstanceResponseDto compute(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        indicator.setText(OpencellBundle.message("tasks.compareScript.progress", scriptInstanceCode, environment.getName()));

        try {
            GetScriptInstanceResponseDto response = OpencellApiService.getInstance(environment).getScript(scriptInstanceCode);

            if (response == null || response.getActionStatus().getStatus() != ActionStatusEnum.SUCCESS || response.getScriptInstance() == null) {
                OpencellNotifier.notifyError(myProject, OpencellBundle.message("tasks.compareScript.notFound", environment.getName()));
            } else {
                return response;
            }
        } catch (IOException e) {
            OpencellNotifier.notifyError(myProject, OpencellBundle.message("tasks.compareScript.error", environment.getName(), e.getMessage()));
        }
        return null;
    }
}
