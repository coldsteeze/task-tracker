package korobkin.nikita.task_service.service.impl;

import jakarta.persistence.criteria.Predicate;
import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
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
import korobkin.nikita.task_service.kafka.producer.TaskEventProducer;
import korobkin.nikita.task_service.mapper.TaskMapper;
import korobkin.nikita.task_service.repository.TaskRepository;
import korobkin.nikita.task_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskEventProducer taskEventProducer;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, Jwt jwt) {
        Task task = taskMapper.toEntity(request);
        task.setUserId(jwt.getSubject());

        taskRepository.saveAndFlush(task);
        log.info("Task with id {} successfully saved", task.getId());

        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(
                task.getId(),
                task.getUserId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().toString(),
                task.getCreatedAt()
        );

        taskEventProducer.sendTaskCreated(taskCreatedEvent);

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getTasks(Jwt jwt, TaskFilterRequest request, Pageable pageable) {
        Page<Task> tasksPage = getUserTasksWithFilters(jwt.getSubject(), request, pageable);

        log.info("Successfully get user tasks with user id {}", jwt.getSubject());

        return taskMapper.toPagedDto(tasksPage);
    }


    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id, Jwt jwt) {
        Task task = getTaskIfOwner(id, jwt);

        log.info("Task with id {} successfully fetched", id);

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskById(UpdateTaskRequest request, UUID id, Jwt jwt) {
        Task task = getTaskIfOwner(id, jwt);

        taskMapper.updateEntityFromDto(request, task);
        taskRepository.saveAndFlush(task);

        log.info("Task with id {} successfully updated", id);

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatusById(UpdateTaskStatusRequest request, UUID id, Jwt jwt) {
        Task task = getTaskIfOwner(id, jwt);
        TaskStatus oldStatus = task.getStatus();

        task.setStatus(request.getStatus());
        taskRepository.saveAndFlush(task);

        log.info("Task with id {} successfully updated status", id);

        TaskStatusChangedEvent taskStatusChangedEvent = new TaskStatusChangedEvent(
                task.getId(),
                task.getUserId(),
                task.getTitle(),
                oldStatus.toString(),
                task.getStatus().toString(),
                task.getUpdatedAt()
        );

        taskEventProducer.sendTaskStatusChanged(taskStatusChangedEvent);

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID id, Jwt jwt) {
        Task task = getTaskIfOwner(id, jwt);

        taskRepository.delete(task);

        log.info("Task with id {} successfully deleted", id);
    }

    private Task getTaskIfOwner(UUID id, Jwt jwt) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getUserId().equals(jwt.getSubject())) {
            throw new TaskAccessDeniedException(ErrorCode.TASK_ACCESS_DENIED);
        }
        return task;
    }


    private Page<Task> getUserTasksWithFilters(String userId, TaskFilterRequest filter, Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));

            if (filter.getSearch() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getSearch().toLowerCase() + "%"));
            }

            if (filter.getTaskStatus() != null) {
                predicates.add(cb.equal(root.get("taskStatus"), filter.getTaskStatus()));
            }

            if (filter.getCreatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter()));
            }

            if (filter.getCreatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore()));
            }

            if (filter.getUpdatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAfter()));
            }

            if (filter.getUpdatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return taskRepository.findAll(spec, pageable);
    }
}
