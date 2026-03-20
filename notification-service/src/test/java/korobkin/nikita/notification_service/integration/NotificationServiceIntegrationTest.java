package korobkin.nikita.notification_service.integration;

import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.notification_service.entity.Notification;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.notification_service.exception.ErrorCode;
import korobkin.nikita.notification_service.exception.NotificationAlreadyReadException;
import korobkin.nikita.notification_service.fixtures.JwtFixtures;
import korobkin.nikita.notification_service.fixtures.NotificationFixtures;
import korobkin.nikita.notification_service.fixtures.TaskEventFixtures;
import korobkin.nikita.notification_service.repository.NotificationRepository;
import korobkin.nikita.notification_service.service.NotificationService;
import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        Jwt jwt = JwtFixtures.createJwtWithUserId(userId);
        Authentication auth = new JwtAuthenticationToken(jwt);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void createTaskCreatedNotification_shouldSaveNotification() {
        TaskCreatedEvent taskEvent = TaskEventFixtures.taskCreatedEvent(userId);

        notificationService.createTaskCreatedNotification(taskEvent);

        List<Notification> notifications = notificationRepository.findAll();

        assertThat(notifications).hasSize(1);

        Notification notification = notifications.getFirst();

        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getType()).isEqualTo(NotificationType.TASK_CREATED);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(notification.getPayload()).isNotNull();
    }

    @Test
    void createStatusChangedNotification_shouldSaveNotification() {
        TaskStatusChangedEvent taskEvent = TaskEventFixtures.taskStatusChangedEvent(userId);

        notificationService.createStatusChangedNotification(taskEvent);

        List<Notification> notifications = notificationRepository.findAll();

        assertThat(notifications).hasSize(1);

        Notification notification = notifications.getFirst();

        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getType()).isEqualTo(NotificationType.TASK_STATUS_CHANGED);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(notification.getPayload()).isNotNull();
    }

    @Test
    void getNotifications_shouldReturnNotifications() {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        PagedResponse<NotificationResponse> response = notificationService.getNotifications(
                new NotificationFilterRequest(),
                PageRequest.of(0, 10)
        );

        AssertionsForInterfaceTypes.assertThat(response.content())
                .extracting(NotificationResponse::type)
                .containsExactly(notification.getType());
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithCreatedType() {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        PagedResponse<NotificationResponse> response = notificationService.getNotifications(
                new NotificationFilterRequest(NotificationType.TASK_CREATED, null, null, null),
                PageRequest.of(0, 10)
        );

        AssertionsForInterfaceTypes.assertThat(response.content())
                .extracting(NotificationResponse::type)
                .containsExactly(notification.getType());
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithStatusRead() {
        Notification notification = NotificationFixtures.notificationWithStatusReadAndPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        PagedResponse<NotificationResponse> response = notificationService.getNotifications(
                new NotificationFilterRequest(null, NotificationStatus.READ, null, null),
                PageRequest.of(0, 10)
        );

        AssertionsForInterfaceTypes.assertThat(response.content())
                .extracting(NotificationResponse::status)
                .containsExactly(notification.getStatus());
    }

    @Test
    void getNotifications_withPageable_shouldReturnCorrectPage() {
        for (int i = 0; i < 15; i++) {
            notificationRepository.save(NotificationFixtures.notificationTaskCreatedWithPayload(
                    userId,
                    TaskEventFixtures.taskCreatedEvent(userId))
            );
        }

        PagedResponse<NotificationResponse> skills = notificationService.getNotifications(
                new NotificationFilterRequest(),
                PageRequest.of(0, 10)
        );

        AssertionsForInterfaceTypes.assertThat(skills.content()).hasSize(10);
        AssertionsForClassTypes.assertThat(skills.totalElements()).isEqualTo(15);
        AssertionsForClassTypes.assertThat(skills.pageNumber()).isEqualTo(0);
        AssertionsForClassTypes.assertThat(skills.totalPages()).isEqualTo(2);
    }

    @Test
    void readNotification_shouldUpdateStatusNotification() {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        notificationService.readNotification(notification.getId());

        List<Notification> notifications = notificationRepository.findAll();

        assertThat(notifications.getFirst().getStatus()).isEqualTo(NotificationStatus.READ);
        assertNotNull(notifications.getFirst().getReadAt());
    }

    @Test
    void readNotification_withAlreadyReadNotification_shouldThrow() {
        Notification notification = NotificationFixtures.notificationWithStatusReadAndPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        NotificationAlreadyReadException ex = assertThrows(NotificationAlreadyReadException.class,
                () -> notificationService.readNotification(notification.getId()));
        assertEquals(ErrorCode.NOTIFICATION_ALREADY_READ, ex.getErrorCode());
    }
}
