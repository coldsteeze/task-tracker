package korobkin.nikita.notification_service.exception;

public class NotificationAlreadyReadException extends AppException {
    public NotificationAlreadyReadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
