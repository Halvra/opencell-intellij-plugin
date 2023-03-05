package com.github.halvra.opencell;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpencellNotifier {
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup("OPENCELL_COMMUNITY_TOOLS_BALLOON");
    private static final NotificationGroup STICKY_NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup("OPENCELL_COMMUNITY_TOOLS_STICKY_BALLOON");
    private static final String PLUGIN_NAME = OpencellBundle.message("plugin.name");

    public static void notifyError(@Nullable Project project, String content) {
        NOTIFICATION_GROUP.createNotification(PLUGIN_NAME, content, NotificationType.ERROR).notify(project);
    }

    public static void notifyInformation(@Nullable Project project, String content) {
        NOTIFICATION_GROUP.createNotification(PLUGIN_NAME, content, NotificationType.INFORMATION).notify(project);
    }

    public static void notifyInformationWithAction(@Nullable Project project, String notificationContent, String actionTitle, Consumer<AnActionEvent> action) {
        STICKY_NOTIFICATION_GROUP.createNotification(PLUGIN_NAME, notificationContent, NotificationType.INFORMATION)
                .addAction(new NotificationAction(actionTitle) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                        action.accept(e);
                        notification.expire();
                    }
                })
                .notify(project);
    }
}
