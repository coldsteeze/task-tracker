package korobkin.nikita.notification_service.controller;

import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponse>> getNotifications(
            NotificationFilterRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(request, pageable));
    }

    @PreAuthorize("@notificationSecurity.isOwner(#p0, authentication)")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> readNotification(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(notificationService.readNotification(id));
    }
}
