package korobkin.nikita.task_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "API error response")
public record ApiError(

        @Schema(example = "404", description = "HTTP status code")
        int status,

        @Schema(example = "Not Found", description = "Short description of the error")
        String error,

        @Schema(example = "Task not found", description = "Detailed error message")
        String message,

        @Schema(example = "TASK_NOT_FOUND", description = "Application specific error code")
        String code,

        @Schema(example = "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000", description = "Path of the endpoint that caused the error")
        String path,

        @Schema(example = "2026-03-17T00:00:00", description = "Timestamp when the error occurred")
        LocalDateTime timestamp
) {}
