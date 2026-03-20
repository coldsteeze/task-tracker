package korobkin.nikita.notification_service.kafka.consumer;

import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.notification_service.service.NotificationService;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskStatusChangedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.taskStatusChanged}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        log.info("received TaskStatusChangedEvent: {}", event);
        notificationService.createTaskEventNotification(event, NotificationType.TASK_STATUS_CHANGED);
    }
}
