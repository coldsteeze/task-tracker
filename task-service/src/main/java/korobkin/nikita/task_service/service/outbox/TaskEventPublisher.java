package korobkin.nikita.task_service.service.outbox;

import korobkin.nikita.task_events.TaskEvent;
import korobkin.nikita.task_service.entity.TaskEventOutbox;
import korobkin.nikita.task_service.entity.enums.TaskEventStatus;
import korobkin.nikita.task_service.kafka.producer.TaskEventProducer;
import korobkin.nikita.task_service.kafka.producer.TaskEventTopicResolver;
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
    private final TaskEventTopicResolver taskEventTopicResolver;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<TaskEventOutbox> events = taskEventOutboxRepository
                .findByStatusAndRetryCountLessThanEqualOrderByCreatedAtAsc(
                        TaskEventStatus.PENDING,
                        5
                );

        for (TaskEventOutbox event : events) {
            try {
                TaskEvent taskEvent = event.getPayload();
                String topic = taskEventTopicResolver.resolve(event.getEventType());
                taskEventProducer.sendSync(topic, taskEvent);
                event.setStatus(TaskEventStatus.SENT);
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                log.error("Failed to send event {}, retry count: {}", event.getId(), event.getRetryCount(), e);
            }
        }
    }
}
