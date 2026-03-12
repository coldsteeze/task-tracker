package korobkin.nikita.auth_service.exception;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
