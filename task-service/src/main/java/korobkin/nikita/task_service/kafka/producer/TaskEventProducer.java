package korobkin.nikita.task_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, String payload) {
        kafkaTemplate.send(topic, payload)
                .thenAccept(result ->
                        log.info("Event sent to topic: {} with offset: {}",
                                topic,
                                result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send event to topic {}: {}", topic, payload, ex);
                    return null;
                });
    }
}
