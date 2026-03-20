package korobkin.nikita.notification_service.integration;

import korobkin.nikita.notification_service.entity.Notification;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.exception.ErrorCode;
import korobkin.nikita.notification_service.fixtures.NotificationFixtures;
import korobkin.nikita.notification_service.fixtures.TaskEventFixtures;
import korobkin.nikita.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NotificationControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
    }

    @Test
    void getNotifications_shouldReturnNotifications() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );
        notificationRepository.save(notification);

        mockMvc.perform(get("/api/v1/notifications")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.content[*].type").value(notification.getType().toString()));
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithCreatedType() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        mockMvc.perform(get("/api/v1/notifications")
                        .param("type", "TASK_CREATED")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.content[*].type").value(notification.getType().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithStatusRead() throws Exception {
        Notification notification = NotificationFixtures.notificationWithStatusReadAndPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        mockMvc.perform(get("/api/v1/notifications")
                        .param("status", "READ")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.content[*].status").value(notification.getStatus().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithCreatedAfter() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.saveAndFlush(notification);

        mockMvc.perform(get("/api/v1/notifications")
                        .param("createdAfter", notification.getCreatedAt().toString())
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.content[*].status").value(notification.getStatus().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getNotifications_shouldReturnNotificationsWithCreatedBefore() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.saveAndFlush(notification);

        LocalDateTime createdBefore = notification.getCreatedAt().plusMinutes(1);

        mockMvc.perform(get("/api/v1/notifications")
                        .param("createdBefore", createdBefore.toString())
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.content[*].status").value(notification.getStatus().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getNotifications_shouldReturnPaginatedResponse() throws Exception {
        for (int i = 0; i < 15; i++) {
            Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                    userId,
                    TaskEventFixtures.taskCreatedEvent(userId)
            );

            notificationRepository.save(notification);
        }

        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "1")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageNumber").value(1));

        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "999")
                        .param("size", "10")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    void readNotification_shouldReturnNotification() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        mockMvc.perform(patch("/api/v1/notifications/" + notification.getId() + "/read")
                        .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notification.getId().toString()))
                .andExpect(jsonPath("$.status").value(NotificationStatus.READ.toString()))
                .andExpect(jsonPath("$.readAt").exists());
    }

    @Test
    void readNotification_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/" + UUID.randomUUID() + "/read"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readNotification_withInvalidUserId_shouldReturnForbidden() throws Exception {
        Notification notification = NotificationFixtures.notificationTaskCreatedWithPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        mockMvc.perform(patch("/api/v1/notifications/" + notification.getId() + "/read")
                        .with(auth(UUID.randomUUID().toString())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOTIFICATION_ACCESS_DENIED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOTIFICATION_ACCESS_DENIED.message));
    }

    @Test
    void readNotification_withInvalidNotificationId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/" + UUID.randomUUID() + "/read")
                        .with(auth(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOTIFICATION_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOTIFICATION_NOT_FOUND.message));
    }

    @Test
    void readNotification_withNotificationAlreadyRead_shouldReturnConflict() throws Exception {
        Notification notification = NotificationFixtures.notificationWithStatusReadAndPayload(
                userId,
                TaskEventFixtures.taskCreatedEvent(userId)
        );

        notificationRepository.save(notification);

        mockMvc.perform(patch("/api/v1/notifications/" + notification.getId() + "/read")
                        .with(auth(userId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOTIFICATION_ALREADY_READ.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOTIFICATION_ALREADY_READ.message));
    }

    private RequestPostProcessor auth(String userId) {
        return jwt()
                .jwt(jwt -> jwt.claim("sub", userId));
    }
}
