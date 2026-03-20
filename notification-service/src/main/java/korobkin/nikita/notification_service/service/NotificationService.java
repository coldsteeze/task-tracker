package korobkin.nikita.notification_service.service;

import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    void createTaskCreatedNotification(TaskCreatedEvent event);

    void createStatusChangedNotification(TaskStatusChangedEvent event);

    PagedResponse<NotificationResponse> getNotifications(NotificationFilterRequest request, Pageable pageable);

    NotificationResponse readNotification(UUID id);
}
