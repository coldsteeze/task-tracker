package korobkin.nikita.task_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    TASK_ACCESS_DENIED("You do not have permission to access this task", HttpStatus.FORBIDDEN),
    TASK_NOT_FOUND("Task not found", HttpStatus.NOT_FOUND);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}