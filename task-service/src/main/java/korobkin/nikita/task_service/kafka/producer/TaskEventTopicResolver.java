package korobkin.nikita.task_service.kafka.producer;

import korobkin.nikita.task_service.config.kafka.KafkaTopicProperties;
import korobkin.nikita.task_service.entity.enums.TaskEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventTopicResolver {

    private final KafkaTopicProperties properties;

    public String resolve(TaskEventType type) {
        return switch (type) {
            case TASK_CREATED -> properties.getTaskCreated();
            case TASK_STATUS_CHANGED -> properties.getTaskStatusChanged();
        };
    }
}
