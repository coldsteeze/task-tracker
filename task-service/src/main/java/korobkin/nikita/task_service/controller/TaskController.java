package korobkin.nikita.task_service.controller;

import jakarta.validation.Valid;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid CreateTaskRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, jwt));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getTasks(
            @AuthenticationPrincipal Jwt jwt,
            TaskFilterRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(jwt, request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.getTaskById(id, jwt));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTaskById(
            @RequestBody @Valid UpdateTaskRequest request,
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.updateTaskById(request, id, jwt));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatusById(
            @RequestBody @Valid UpdateTaskStatusRequest request,
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.updateTaskStatusById(request, id, jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        taskService.deleteTaskById(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
