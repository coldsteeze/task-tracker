package korobkin.nikita.task_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.task_service.entity.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO representing task data")
public record TaskResponse(

        @Schema(description = "Unique task identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the user who owns the task", example = "user-123")
        String userId,

        @Schema(description = "Task title", example = "Implement authentication")
        String title,

        @Schema(description = "Task description", example = "Add JWT-based authentication")
        String description,

        @Schema(description = "Current task status", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Task creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Task last update timestamp")
        LocalDateTime updatedAt
) {
}
