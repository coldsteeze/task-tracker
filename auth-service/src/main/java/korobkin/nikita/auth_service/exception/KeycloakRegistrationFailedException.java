package korobkin.nikita.auth_service.exception;

public class KeycloakRegistrationFailedException extends AppException {
    public KeycloakRegistrationFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
