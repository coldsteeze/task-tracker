package korobkin.nikita.notification_service.fixtures;

import korobkin.nikita.notification_service.entity.Notification;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.task_events.TaskEvent;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationFixtures {

    public static Notification notificationTaskCreatedWithPayload(String userId, TaskEvent payload) {
        return notification(userId, NotificationType.TASK_CREATED, NotificationStatus.UNREAD, payload);
    }

    public static Notification notificationWithStatusReadAndPayload(String userId, TaskEvent payload) {
        return notification(userId, NotificationType.TASK_CREATED, NotificationStatus.READ, payload);
    }

    public static Notification notification(
            String userId,
            NotificationType type,
            NotificationStatus status,
            TaskEvent payload) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setStatus(status);
        notification.setPayload(payload);

        return notification;
    }


}
