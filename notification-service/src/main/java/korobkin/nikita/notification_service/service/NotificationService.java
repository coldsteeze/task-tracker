package korobkin.nikita.notification_service.service;

import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.task_events.TaskEvent;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    void createTaskEventNotification(TaskEvent event, NotificationType type);

    PagedResponse<NotificationResponse> getNotifications(NotificationFilterRequest request, Pageable pageable);

    NotificationResponse readNotification(UUID id);
}
