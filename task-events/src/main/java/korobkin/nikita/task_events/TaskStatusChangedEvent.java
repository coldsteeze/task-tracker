package korobkin.nikita.task_events;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskStatusChangedEvent(
        UUID taskId,
        String userId,
        String title,
        String oldStatus,
        String newStatus,
        LocalDateTime updatedAt
) implements TaskEvent {}
