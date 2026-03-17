package korobkin.nikita.task_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request DTO for creating a new task")
@Getter
@Setter
public class CreateTaskRequest {

    @Schema(description = "Task title", example = "Implement authentication")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Task description", example = "Add JWT-based authentication")
    @Size(max = 5000, message = "Description too long")
    private String description;

    @Schema(description = "Initial task status", example = "TODO")
    @NotNull(message = "Status is required")
    private TaskStatus status;
}
