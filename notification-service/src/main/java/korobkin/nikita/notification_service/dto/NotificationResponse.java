package korobkin.nikita.notification_service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        NotificationStatus status,
        JsonNode payload,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
