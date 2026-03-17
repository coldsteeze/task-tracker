package korobkin.nikita.task_service.kafka.producer;

import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import korobkin.nikita.task_service.config.kafka.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public void sendTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(topics.getTaskCreated(), event);
        log.info("TaskCreated event sent to topic: {} with {}", topics.getTaskCreated(), event);
    }

    public void sendTaskStatusChanged(TaskStatusChangedEvent event) {
        kafkaTemplate.send(topics.getTaskStatusChanged(), event);
        log.info("TaskStatusChanged event sent to topic: {} with {}", topics.getTaskStatusChanged(), event);
    }
}
