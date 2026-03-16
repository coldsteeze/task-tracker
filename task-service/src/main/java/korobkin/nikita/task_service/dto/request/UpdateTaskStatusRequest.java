package korobkin.nikita.task_service.dto.request;

import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskStatusRequest {

    private TaskStatus status;
}
