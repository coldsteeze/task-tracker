package korobkin.nikita.task_service.exception;

public class TaskAccessDeniedException extends AppException {
    public TaskAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
