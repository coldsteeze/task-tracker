package korobkin.nikita.task_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request DTO for updating only task status")
@Getter
@Setter
public class UpdateTaskStatusRequest {

    @Schema(description = "New task status", example = "IN_PROGRESS")
    private TaskStatus status;
}
