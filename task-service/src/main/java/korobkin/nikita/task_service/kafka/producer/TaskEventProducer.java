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
        kafkaTemplate.send(topics.getTaskCreated(), event)
                .thenAccept(result ->
                        log.info("TaskCreated event sent to topic: {} with offset: {}",
                                topics.getTaskCreated(),
                                result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send TaskCreated event: {}", event, ex);
                    return null;
                });
    }

    public void sendTaskStatusChanged(TaskStatusChangedEvent event) {
        kafkaTemplate.send(topics.getTaskStatusChanged(), event)
                .thenAccept(result ->
                        log.info("TaskStatusChanged event sent to topic: {} with {}",
                                topics.getTaskStatusChanged(),
                                result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send TaskStatusChanged event: {}", event, ex);
                    return null;
                });
        log.info("TaskStatusChanged event sent to topic: {} with {}", topics.getTaskStatusChanged(), event);
    }
}
