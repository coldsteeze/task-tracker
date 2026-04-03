package korobkin.nikita.notification_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    NOTIFICATION_ACCESS_DENIED("You do not have permission to access this notification", HttpStatus.FORBIDDEN),
    NOTIFICATION_NOT_FOUND("Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_ALREADY_READ("Notification with this id already read", HttpStatus.CONFLICT);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
