package korobkin.nikita.task_service.fixtures;

import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskRequestFixtures {

    public static final String VALID_TITLE = "task 1";
    public static final String VALID_UPDATED_TITLE = "Updated task 1";

    public static CreateTaskRequest validCreateTaskRequest() {
        return createTaskRequest(VALID_TITLE, null, TaskStatus.IN_PROGRESS);
    }

    public static CreateTaskRequest createTaskRequestWithTitle(String title) {
        return createTaskRequest(title, null, TaskStatus.IN_PROGRESS);
    }

    public static UpdateTaskRequest validUpdateTaskRequest() {
        return updateTaskRequest(VALID_UPDATED_TITLE, null, TaskStatus.IN_PROGRESS);
    }

    public static UpdateTaskRequest updateTaskRequestWithTitle(String title) {
        return updateTaskRequest(title, null, TaskStatus.IN_PROGRESS);
    }

    public static CreateTaskRequest createTaskRequest(String title, String description, TaskStatus status) {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setStatus(status);

        return request;
    }

    public static UpdateTaskRequest updateTaskRequest(String title, String description, TaskStatus status) {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setStatus(status);

        return request;
    }

    public static UpdateTaskStatusRequest updateTaskStatusRequest(TaskStatus status) {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(status);

        return request;
    }
}
