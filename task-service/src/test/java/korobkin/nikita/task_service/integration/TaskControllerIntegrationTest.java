package korobkin.nikita.task_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.entity.Task;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import korobkin.nikita.task_service.exception.ErrorCode;
import korobkin.nikita.task_service.fixtures.TaskFixtures;
import korobkin.nikita.task_service.fixtures.TaskRequestFixtures;
import korobkin.nikita.task_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        CreateTaskRequest request = TaskRequestFixtures.validCreateTaskRequest();

        mockMvc.perform(post("/api/v1/tasks")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.status").value(request.getStatus().toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void createTask_withBlankTitle_shouldReturnBadRequest(String invalidTitle) throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.createTaskRequestWithTitle(invalidTitle))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_shouldReturnUnauthorized() throws Exception {
        CreateTaskRequest request = TaskRequestFixtures.validCreateTaskRequest();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTasks_shouldReturnTasks() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(task.getId().toString()))
                .andExpect(jsonPath("$.content[*].title").value(task.getTitle()));
    }

    @Test
    void getTasks_shouldReturnTasksWithStatusDone() throws Exception {
        Task firstTask = TaskFixtures.validTaskWithStatus(userId, TaskStatus.IN_PROGRESS);
        Task secondTask = TaskFixtures.validTaskWithStatus(userId, TaskStatus.DONE);
        taskRepository.save(firstTask);
        taskRepository.save(secondTask);

        mockMvc.perform(get("/api/v1/tasks")
                        .with(auth(userId))
                        .param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(secondTask.getId().toString()))
                .andExpect(jsonPath("$.content[*].title").value(secondTask.getTitle()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTasks_withIncompleteTitle_shouldReturnTasks() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks")
                        .with(auth(userId))
                        .param("search", "tas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(task.getId().toString()))
                .andExpect(jsonPath("$.content[*].title").value(task.getTitle()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTasks_shouldReturnPaginatedResponse() throws Exception {
        for (int i = 0; i < 15; i++) {
            Task task = TaskFixtures.validTask(userId);
            taskRepository.save(task);
        }

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "1")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageNumber").value(1));

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "999")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks/" + task.getId())
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value(task.getStatus().toString()));
    }

    @Test
    void getTaskById_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTaskById_shouldReturnAccessDenied() throws Exception {
        Task task = TaskFixtures.validTask(UUID.randomUUID().toString());
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks/" + task.getId())
                        .with(auth(userId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_ACCESS_DENIED.message));
    }

    @Test
    void getTaskById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/" + UUID.randomUUID())
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_NOT_FOUND.message));
    }

    @Test
    void updateTaskById_shouldReturnTask() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskRequest request = TaskRequestFixtures.validUpdateTaskRequest();

        mockMvc.perform(put("/api/v1/tasks/" + task.getId())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.status").value(request.getStatus().toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

    }

    @Test
    void updateTaskById_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.validUpdateTaskRequest())))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void updateTaskById_withBlankTitle_shouldReturnBadRequest(String invalidTitle) throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        mockMvc.perform(put("/api/v1/tasks/" + task.getId())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.updateTaskRequestWithTitle(invalidTitle))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTaskById_shouldReturnAccessDenied() throws Exception {
        Task task = TaskFixtures.validTask(UUID.randomUUID().toString());
        taskRepository.save(task);

        mockMvc.perform(put("/api/v1/tasks/" + task.getId())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.validUpdateTaskRequest())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_ACCESS_DENIED.message));
    }

    @Test
    void updateTaskById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/tasks/" + UUID.randomUUID())
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.validUpdateTaskRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_NOT_FOUND.message));
    }

    @Test
    void updateTaskStatusById_shouldReturnTask() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        UpdateTaskStatusRequest request = TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE);

        mockMvc.perform(patch("/api/v1/tasks/" + task.getId() + "/status")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value(request.getStatus().toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateTaskStatusById_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/" + UUID.randomUUID() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateTaskStatusById_shouldReturnAccessDenied() throws Exception {
        Task task = TaskFixtures.validTask(UUID.randomUUID().toString());
        taskRepository.save(task);

        mockMvc.perform(patch("/api/v1/tasks/" + task.getId() + "/status")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_ACCESS_DENIED.message));
    }

    @Test
    void updateTaskStatusById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/" + UUID.randomUUID() + "/status")
                        .with(auth(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(TaskRequestFixtures.updateTaskStatusRequest(TaskStatus.DONE))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_NOT_FOUND.message));
    }

    @Test
    void deleteTaskById_shouldReturnNoContent() throws Exception {
        Task task = TaskFixtures.validTask(userId);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/v1/tasks/" + task.getId())
                        .with(auth(userId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaskById_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteTaskById_shouldReturnAccessDenied() throws Exception {
        Task task = TaskFixtures.validTask(UUID.randomUUID().toString());
        taskRepository.save(task);

        mockMvc.perform(delete("/api/v1/tasks/" + task.getId())
                        .with(auth(userId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_ACCESS_DENIED.message));
    }

    @Test
    void deleteTaskById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/" + UUID.randomUUID())
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.TASK_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TASK_NOT_FOUND.message));
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private RequestPostProcessor auth(String userId) {
        return jwt()
                .jwt(jwt -> jwt.claim("sub", userId));
    }
}
