package korobkin.nikita.notification_service.service.impl;

import jakarta.persistence.criteria.Predicate;
import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.notification_service.entity.Notification;
import korobkin.nikita.notification_service.entity.Notification_;
import korobkin.nikita.notification_service.entity.enums.NotificationStatus;
import korobkin.nikita.notification_service.entity.enums.NotificationType;
import korobkin.nikita.notification_service.exception.ErrorCode;
import korobkin.nikita.notification_service.exception.NotificationAlreadyReadException;
import korobkin.nikita.notification_service.mapper.NotificationMapper;
import korobkin.nikita.notification_service.repository.NotificationRepository;
import korobkin.nikita.notification_service.service.NotificationService;
import korobkin.nikita.task_events.TaskCreatedEvent;
import korobkin.nikita.task_events.TaskStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createTaskCreatedNotification(TaskCreatedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.userId());
        notification.setType(NotificationType.TASK_CREATED);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setPayload(event);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createStatusChangedNotification(TaskStatusChangedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.userId());
        notification.setType(NotificationType.TASK_STATUS_CHANGED);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setPayload(event);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getNotifications(
            NotificationFilterRequest request,
            Pageable pageable) {
        String currentUserId = getCurrentUserId();

        Page<Notification> tasksPage = getUserNotificationsWithFilters(currentUserId, request, pageable);

        log.info("Successfully get user notifications with user id {}", currentUserId);

        return notificationMapper.toPagedDto(tasksPage);
    }

    @Override
    @Transactional
    public NotificationResponse readNotification(UUID id) {
        Notification notification = notificationRepository.getReferenceById(id);

        if (notification.getStatus() == NotificationStatus.READ) {
            throw new NotificationAlreadyReadException(ErrorCode.NOTIFICATION_ALREADY_READ);
        }

        notification.setStatus(NotificationStatus.READ);

        log.info("Notification with id {} successfully read", id);

        return notificationMapper.toResponse(notification);
    }


    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) (authentication != null ? authentication.getPrincipal() : null);

        return jwt != null ? jwt.getSubject() : null;
    }

    private Page<Notification> getUserNotificationsWithFilters(String userId, NotificationFilterRequest filter, Pageable pageable) {
        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(Notification_.userId), userId));

            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get(Notification_.type), filter.getType()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get(Notification_.status), filter.getStatus()));
            }

            if (filter.getCreatedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Notification_.createdAt), filter.getCreatedAfter()));
            }

            if (filter.getCreatedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Notification_.createdAt), filter.getCreatedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return notificationRepository.findAll(spec, pageable);
    }
}
