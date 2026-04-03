package korobkin.nikita.notification_service.exception;

public class NotificationAccessDeniedException extends AppException {
    public NotificationAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
