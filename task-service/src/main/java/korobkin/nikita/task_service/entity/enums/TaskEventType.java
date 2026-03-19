package korobkin.nikita.task_service.entity.enums;

import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;

public enum TaskEventType {

    TASK_CREATED,
    TASK_STATUS_CHANGED;

    public static TaskEventType fromEvent(TaskEvent event) {
        return switch (event) {
            case TaskCreatedEvent e -> TASK_CREATED;
            case TaskStatusChangedEvent e -> TASK_STATUS_CHANGED;
        };
    }
}
