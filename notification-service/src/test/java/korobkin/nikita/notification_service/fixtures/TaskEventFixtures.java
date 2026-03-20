package korobkin.nikita.notification_service.fixtures;

import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TaskEventFixtures {

    public TaskCreatedEvent taskCreatedEvent(String userId) {
        return new TaskCreatedEvent(
                UUID.randomUUID(),
                userId,
                "title",
                "description",
                "IN_PROGRESS",
                LocalDateTime.now()
        );
    }

    public TaskStatusChangedEvent taskStatusChangedEvent(String userId) {
        return new TaskStatusChangedEvent(
                UUID.randomUUID(),
                userId,
                "title",
                "IN_PROGRESS",
                "DONE",
                LocalDateTime.now()
        );
    }
}
