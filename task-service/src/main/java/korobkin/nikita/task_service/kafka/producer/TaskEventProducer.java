package korobkin.nikita.task_service.kafka.producer;

import korobkin.nikita.task_events.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSync(String topic, TaskEvent event) {
        try {
            var result = kafkaTemplate.send(topic, event).get(10, TimeUnit.SECONDS);
            log.info("Event sent to topic: {} with offset: {}", topic, result.getRecordMetadata().offset());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Failed to send event to topic " + topic, e);
        }
    }
}
