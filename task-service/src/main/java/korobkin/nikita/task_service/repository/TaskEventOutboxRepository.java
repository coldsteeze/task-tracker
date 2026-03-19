package korobkin.nikita.task_service.repository;

import korobkin.nikita.task_service.entity.TaskEventOutbox;
import korobkin.nikita.task_service.entity.enums.TaskEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskEventOutboxRepository extends JpaRepository<TaskEventOutbox, UUID> {

    List<TaskEventOutbox> findByStatusAndRetryCountLessThanEqualOrderByCreatedAtAsc(TaskEventStatus status, Integer retryCount);
}
