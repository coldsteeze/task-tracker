package korobkin.nikita.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.task_events.TaskEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Notification response DTO")
public record NotificationResponse(

        @Schema(description = "Unique identifier of the notification", example = "9bd69c63-3ef0-4295-b6c1-f6f8e5b44e99")
        UUID id,

        @Schema(description = "Type of the notification", example = "TASK_CREATED")
        NotificationType type,

        @Schema(description = "Current status of the notification", example = "UNREAD")
        NotificationStatus status,

        @Schema(description = "Payload of the notification, can contain event-specific data")
        TaskEvent payload,

        @Schema(description = "Timestamp when the notification was created", example = "2026-03-20T09:01:37")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the notification was read, null if unread", example = "2026-03-20T10:15:00")
        LocalDateTime readAt
) {}