package korobkin.nikita.task_events;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCreatedEvent(
        UUID taskId,
        String userId,
        String title,
        String description,
        String status,
        LocalDateTime createdAt
) implements TaskEvent {}
