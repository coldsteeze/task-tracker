package korobkin.nikita.task_events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskCreatedEvent.class, name = "TASK_CREATED"),
        @JsonSubTypes.Type(value = TaskStatusChangedEvent.class, name = "TASK_STATUS_CHANGED")
})
public sealed interface TaskEvent permits TaskCreatedEvent, TaskStatusChangedEvent {

    String userId();
}
