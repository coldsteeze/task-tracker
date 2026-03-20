package korobkin.nikita.notification_service.security;

import korobkin.nikita.notification_service.entity.Notification;
import korobkin.nikita.notification_service.exception.ErrorCode;
import korobkin.nikita.notification_service.exception.NotificationAccessDeniedException;
import korobkin.nikita.notification_service.exception.NotificationNotFoundException;
import korobkin.nikita.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSecurity {

    private final NotificationRepository notificationRepository;

    public boolean isOwner(UUID notificationId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(jwt != null ? jwt.getSubject() : null)) {
            throw new NotificationAccessDeniedException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        return true;
    }
}
