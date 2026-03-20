package korobkin.nikita.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request filter DTO for searching notifications")
public class NotificationFilterRequest {

    @Schema(description = "Filter by notification type", example = "TASK_CREATED")
    private NotificationType type;

    @Schema(description = "Filter by notification status", example = "UNREAD")
    private NotificationStatus status;

    @Schema(description = "Filter by creation date (start, optional)")
    private LocalDateTime createdAfter;

    @Schema(description = "Filter by creation date (end, optional)")
    private LocalDateTime createdBefore;
}
