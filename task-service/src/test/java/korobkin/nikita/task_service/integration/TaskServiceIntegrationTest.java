package korobkin.nikita.task_service.integration;

import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.TaskFilterRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.dto.response.PagedResponse;
import korobkin.nikita.task_service.dto.response.TaskResponse;
import korobkin.nikita.task_service.entity.Task;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import korobkin.nikita.task_service.exception.ErrorCode;
import korobkin.nikita.task_service.exception.TaskAccessDeniedException;
import korobkin.nikita.task_service.exception.TaskNotFoundException;
import korobkin.nikita.task_service.fixtures.JwtFixtures;
import korobkin.nikita.task_service.fixtures.TaskFixtures;
import korobkin.nikita.task_service.fixtures.TaskRequestFixtures;
import korobkin.nikita.task_service.kafka.producer.TaskEventProducer;
import korobkin.nikita.task_service.repository.TaskRepository;
import korobkin.nikita.task_service.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class TaskServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @MockitoSpyBean
    private TaskEventProducer taskEventProducer;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
    }

    @Test
    void createTask_shouldSaveTask() {
        CreateTaskRequest request = TaskRequestFixtures.validCreateTaskRequest();

        TaskResponse response = taskService.createTask(request, JwtFixtures.createJwtWithUserId(userId));

        Task task = taskRepository.findById(response.id())
                .orElseThrow(() -> new AssertionError("Task not found in DB"));

        assertEquals(request.getTitle(), task.getTitle());
        assertEquals(request.getDescription(), task.getDescription());
        assertEquals(request.getStatus(), task.getStatus());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    void createTask_shouldSendEvent() {
        CreateTaskRequest request = TaskRequestFixtures.validCreateTaskRequest();

        taskService.createTask(request, JwtFixtures.createJwtWithUserId(userId));

        Mockito.verify(taskEventProducer).sendTaskCreated(any(TaskCreatedEvent.class));
    }

    @Test
    void getTasks_shouldReturnTasks() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        PagedResponse<TaskResponse> response = taskService.getTasks(
                JwtFixtures.createJwtWithUserId(userId),
                new TaskFilterRequest(),
                PageRequest.of(0, 10)
        );

        assertThat(response.content())
                .extracting(TaskResponse::title)
                .containsExactly(task.getTitle());
    }

    @Test
    void getTasks_shouldReturnTasksWithStatusDone() {
        Task task = TaskFixtures.validTaskWithStatus(userId, TaskStatus.DONE);
        taskRepository.save(task);

        PagedResponse<TaskResponse> response = taskService.getTasks(
                JwtFixtures.createJwtWithUserId(userId),
                new TaskFilterRequest(null, TaskStatus.DONE, null, null, null, null),
                PageRequest.of(0, 10)
        );

        assertThat(response.content())
                .extracting(TaskResponse::title)
                .containsExactly(task.getTitle());
    }

    @Test
    void getTasks_withIncompleteTitle_shouldReturnTasks() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        PagedResponse<TaskResponse> response = taskService.getTasks(
                JwtFixtures.createJwtWithUserId(userId),
                new TaskFilterRequest("tas", null, null, null, null, null),
                PageRequest.of(0, 10)
        );

        assertThat(response.content())
                .extracting(TaskResponse::title)
                .containsExactly(task.getTitle());
    }

    @Test
    void getTasks_withPageable_shouldReturnCorrectPage() {
        for (int i = 0; i < 15; i++) {
            taskRepository.save(TaskFixtures.validTask(userId));
        }

        PagedResponse<TaskResponse> skills = taskService.getTasks(
                JwtFixtures.createJwtWithUserId(userId),
                new TaskFilterRequest(),
                PageRequest.of(0, 10)
        );

        assertThat(skills.content()).hasSize(10);
        assertThat(skills.totalElements()).isEqualTo(15);
        assertThat(skills.pageNumber()).isEqualTo(0);
        assertThat(skills.totalPages()).isEqualTo(2);
    }

    @Test
    void getTaskById_shouldReturnTask() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        TaskResponse response = taskService.getTaskById(task.getId(), JwtFixtures.createJwtWithUserId(userId));

        assertEquals(response.id(), task.getId());
        assertEquals(response.userId(), task.getUserId());
        assertEquals(response.title(), task.getTitle());
        assertEquals(response.description(), task.getDescription());
        assertEquals(response.status(), task.getStatus());
        assertEquals(response.createdAt(), task.getCreatedAt());
        assertEquals(response.updatedAt(), task.getUpdatedAt());
    }

    @Test
    void getTaskById_withInvalidUserId_shouldThrow() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        TaskAccessDeniedException ex = assertThrows(TaskAccessDeniedException.class,
                () -> taskService.getTaskById(
                        task.getId(),
                        JwtFixtures.createJwtWithUserId(UUID.randomUUID().toString()))
        );

        assertEquals(ErrorCode.TASK_ACCESS_DENIED, ex.getErrorCode());
    }

    @Test
    void getTaskById_withInvalidTaskId_shouldThrow() {
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(
                        UUID.randomUUID(),
                        JwtFixtures.createJwtWithUserId(userId))
        );

        assertEquals(ErrorCode.TASK_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateTaskById_shouldUpdateTask() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskRequest request = TaskRequestFixtures.validUpdateTaskRequest();

        taskService.updateTaskById(request, task.getId(), JwtFixtures.createJwtWithUserId(userId));

        task = taskRepository.findById(task.getId())
                .orElseThrow(() -> new AssertionError("Task not found in DB"));

        assertEquals(request.getTitle(), task.getTitle());
        assertNotEquals(task.getUpdatedAt(), task.getCreatedAt());
    }

    @Test
    void updateTaskById_withInvalidUserId_shouldThrow() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskRequest request = TaskRequestFixtures.validUpdateTaskRequest();

        TaskAccessDeniedException ex = assertThrows(TaskAccessDeniedException.class,
                () -> taskService.updateTaskById(
                        request,
                        task.getId(),
                        JwtFixtures.createJwtWithUserId(UUID.randomUUID().toString()))
        );

        assertEquals(ErrorCode.TASK_ACCESS_DENIED, ex.getErrorCode());
    }

    @Test
    void updateTaskById_withInvalidTaskId_shouldThrow() {
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTaskById(
                        TaskRequestFixtures.validUpdateTaskRequest(),
                        UUID.randomUUID(),
                        JwtFixtures.createJwtWithUserId(userId))
        );

        assertEquals(ErrorCode.TASK_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateTaskStatusById_shouldUpdateTaskStatus() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskStatusRequest request = TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE);

        taskService.updateTaskStatusById(request, task.getId(), JwtFixtures.createJwtWithUserId(userId));

        task = taskRepository.findById(task.getId())
                .orElseThrow(() -> new AssertionError("Task not found in DB"));

        assertEquals(request.getStatus(), task.getStatus());
        assertNotEquals(task.getUpdatedAt(), task.getCreatedAt());
    }

    @Test
    void updateTaskStatusById_withInvalidUserId_shouldThrow() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskStatusRequest request = TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE);

        TaskAccessDeniedException ex = assertThrows(TaskAccessDeniedException.class,
                () -> taskService.updateTaskStatusById(
                        request,
                        task.getId(),
                        JwtFixtures.createJwtWithUserId(UUID.randomUUID().toString()))
        );

        assertEquals(ErrorCode.TASK_ACCESS_DENIED, ex.getErrorCode());
    }

    @Test
    void updateTaskStatusById_withInvalidTaskId_shouldThrow() {
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTaskStatusById(
                        TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE),
                        UUID.randomUUID(),
                        JwtFixtures.createJwtWithUserId(userId))
        );

        assertEquals(ErrorCode.TASK_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteTaskById_shouldDeleteTask() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        taskService.deleteTaskById(task.getId(), JwtFixtures.createJwtWithUserId(userId));

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    void deleteTaskById_withInvalidUserId_shouldThrow() {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        TaskAccessDeniedException ex = assertThrows(TaskAccessDeniedException.class,
                () -> taskService.deleteTaskById(
                        task.getId(),
                        JwtFixtures.createJwtWithUserId(UUID.randomUUID().toString()))
        );

        assertEquals(ErrorCode.TASK_ACCESS_DENIED, ex.getErrorCode());
    }

    @Test
    void deleteTaskById_withInvalidTaskId_shouldThrow() {
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTaskById(
                        UUID.randomUUID(),
                        JwtFixtures.createJwtWithUserId(userId))
        );

        assertEquals(ErrorCode.TASK_NOT_FOUND, ex.getErrorCode());
    }
}
