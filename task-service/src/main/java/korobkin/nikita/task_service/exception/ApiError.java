package korobkin.nikita.task_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "API error response")
public record ApiError(

        @Schema(example = "409", description = "HTTP status code")
        int status,

        @Schema(example = "Conflict", description = "Short description of the error")
        String error,

        @Schema(example = "User already exists", description = "Detailed error message")
        String message,

        @Schema(example = "USER_ALREADY_EXISTS", description = "Application specific error code")
        String code,

        @Schema(example = "/api/v1/auth/register", description = "Path of the endpoint that caused the error")
        String path,

        @Schema(example = "2026-03-16T00:00:00", description = "Timestamp when the error occurred")
        LocalDateTime timestamp
) {}
