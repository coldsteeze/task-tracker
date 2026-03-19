package korobkin.nikita.task_service.service;

import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.TaskFilterRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.dto.response.PagedResponse;
import korobkin.nikita.task_service.dto.response.TaskResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    PagedResponse<TaskResponse> getTasks(TaskFilterRequest request, Pageable pageable);

    TaskResponse getTaskById(UUID id);

    TaskResponse updateTaskById(UpdateTaskRequest request, UUID id);

    TaskResponse updateTaskStatusById(UpdateTaskStatusRequest request, UUID id);

    void deleteTaskById(UUID id);
}
