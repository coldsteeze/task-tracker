package korobkin.nikita.notification_service.exception;

public class NotificationNotFoundException extends AppException {
    public NotificationNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
