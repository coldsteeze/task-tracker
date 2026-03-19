package korobkin.nikita.task_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request DTO for updating task")
@Getter
@Setter
public class UpdateTaskRequest {

    @Schema(description = "Updated task title", example = "Fix authentication bug")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Updated task description", example = "Fix JWT validation issue")
    @Size(max = 5000, message = "Title too long")
    private String description;

    @Schema(description = "Updated task status", example = "DONE")
    @NotNull(message = "Status is required")
    private TaskStatus status;
}
