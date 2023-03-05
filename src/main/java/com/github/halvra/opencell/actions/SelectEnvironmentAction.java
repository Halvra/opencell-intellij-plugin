package com.github.halvra.opencell.actions;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import icons.Icons;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@Getter
final class SelectEnvironmentAction extends AnAction {
    private final Project project;
    private final Environment environment;
    private final BiConsumer<AnActionEvent, SelectEnvironmentAction> actionPerformedConsumer;

    SelectEnvironmentAction(final Project project, final Environment environment, final BiConsumer<AnActionEvent, SelectEnvironmentAction> actionPerformedConsumer) {
        this.project = project;
        this.environment = environment;
        this.actionPerformedConsumer = actionPerformedConsumer;

        String name = environment.getName();
        Presentation presentation = getTemplatePresentation();
        presentation.setText(name, false);
        presentation.setDescription(OpencellBundle.message("select.0", name));

        presentation.setIcon(environment.isPreferred() ? SelectEnvironmentComboBoxAction.CHECKED_ICON : Icons.OPENCELL);
        presentation.setSelectedIcon(environment.isPreferred() ? SelectEnvironmentComboBoxAction.CHECKED_SELECTED_ICON : Icons.OPENCELL);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        actionPerformedConsumer.accept(e, this);
    }
}
