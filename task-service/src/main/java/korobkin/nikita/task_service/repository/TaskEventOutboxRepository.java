package korobkin.nikita.task_service.repository;

import korobkin.nikita.task_service.entity.TaskEventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskEventOutboxRepository extends JpaRepository<TaskEventOutbox, UUID> {
}
