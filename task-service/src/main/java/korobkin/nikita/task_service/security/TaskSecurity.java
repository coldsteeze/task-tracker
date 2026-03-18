package korobkin.nikita.task_service.security;

import korobkin.nikita.task_service.entity.Task;
import korobkin.nikita.task_service.exception.ErrorCode;
import korobkin.nikita.task_service.exception.TaskAccessDeniedException;
import korobkin.nikita.task_service.exception.TaskNotFoundException;
import korobkin.nikita.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public boolean isOwner(UUID taskId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getUserId().equals(jwt != null ? jwt.getSubject() : null)) {
            throw new TaskAccessDeniedException(ErrorCode.TASK_ACCESS_DENIED);
        }

        return true;
    }
}
