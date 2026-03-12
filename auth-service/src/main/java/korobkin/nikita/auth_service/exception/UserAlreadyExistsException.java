package korobkin.nikita.auth_service.exception;

public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
