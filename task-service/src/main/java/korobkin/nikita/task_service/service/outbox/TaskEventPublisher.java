package korobkin.nikita.task_service.service.outbox;

import korobkin.nikita.task_service.entity.TaskEventOutbox;
import korobkin.nikita.task_service.kafka.producer.TaskEventProducer;
import korobkin.nikita.task_service.repository.TaskEventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final TaskEventOutboxRepository taskEventOutboxRepository;
    private final TaskEventProducer taskEventProducer;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<TaskEventOutbox> events = taskEventOutboxRepository.findAll();

        for (TaskEventOutbox event : events) {
            try {
                taskEventProducer.send(event.getEventType(), event.getPayload());
                log.info("Sent event {} to Kafka", event.getId());

                taskEventOutboxRepository.delete(event);
            } catch (Exception e) {
                log.error("Failed to send event {} to Kafka, will retry later", event.getId(), e);
            }
        }
    }
}
