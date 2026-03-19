package korobkin.nikita.task_service.controller;

import jakarta.validation.Valid;
import korobkin.nikita.task_service.docs.TaskControllerDocs;
import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.TaskFilterRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.dto.response.PagedResponse;
import korobkin.nikita.task_service.dto.response.TaskResponse;
import korobkin.nikita.task_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController implements TaskControllerDocs {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getTasks(
            TaskFilterRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(request, pageable));
    }

    @PreAuthorize("@taskSecurity.isOwner(#p0, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PreAuthorize("@taskSecurity.isOwner(#p1, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTaskById(
            @RequestBody @Valid UpdateTaskRequest request,
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.updateTaskById(request, id));
    }

    @PreAuthorize("@taskSecurity.isOwner(#p1, authentication)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatusById(
            @RequestBody @Valid UpdateTaskStatusRequest request,
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.updateTaskStatusById(request, id));
    }

    @PreAuthorize("@taskSecurity.isOwner( #p0, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable("id") UUID id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
