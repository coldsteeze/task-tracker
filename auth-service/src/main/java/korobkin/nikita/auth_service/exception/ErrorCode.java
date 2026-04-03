package korobkin.nikita.auth_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_CREDENTIALS("Invalid username or password", HttpStatus.UNAUTHORIZED),
    KEYCLOAK_REGISTRATION_FAILED("User registration failed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("User already exists", HttpStatus.CONFLICT);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
