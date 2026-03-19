package korobkin.nikita.task_service.exception;

public class TaskNotFoundException extends AppException{
    public TaskNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
