package korobkin.nikita.notification_service.kafka.consumer;

import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.notification_service.service.NotificationService;
import korobkin.nikita.task_events.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskCreatedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.taskCreated}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("received TaskCreatedEvent: {}", event);
        notificationService.createTaskEventNotification(event, NotificationType.TASK_CREATED);
    }
}
