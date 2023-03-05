package com.github.halvra.opencell.listeners;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.OpencellNotifier;
import com.github.halvra.opencell.settings.ProjectSettingsState;
import com.github.halvra.opencell.utils.CompatibilityUtil;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class OpencellProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        var settings = ProjectSettingsState.getInstance(project);
        if (CompatibilityUtil.haveRequiredDependencies(project) && !settings.isScriptInterfacesAutoDetected()) {
            OpencellNotifier.notifyInformationWithAction(project,
                    OpencellBundle.message("notifications.scriptInterfaces.autoDetect"),
                    OpencellBundle.message("notifications.scriptInterfaces.autoDetect.action"),
                    anActionEvent -> {
                        var detectedScriptInterfaces = ScriptUtil.getScriptsInterfaces(project);
                        detectedScriptInterfaces.forEach(scriptInterface -> {
                            if (!settings.getScriptInterfaces().contains(scriptInterface)) {
                                settings.getScriptInterfaces().add(scriptInterface);
                            }
                        });
                        settings.setScriptInterfacesAutoDetected(true);
                        OpencellNotifier.notifyInformation(project, OpencellBundle.message("notifications.scriptInterfaces.autoDetect.result", settings.getScriptInterfaces().size()));
                    }
            );
        }
    }
}
