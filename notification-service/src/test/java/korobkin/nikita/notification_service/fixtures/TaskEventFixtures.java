package korobkin.nikita.notification_service.fixtures;

import korobkin.nikita.task_events.TaskCreatedEvent;
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
}
