package korobkin.nikita.task_service.fixtures;

import korobkin.nikita.task_service.entity.Task;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskFixtures {

    public static final String VALID_TITLE = "task";
    public static final String VALID_DESCRIPTION = "description";

    public static Task validTask(String userId) {
        return task(userId, VALID_TITLE, VALID_DESCRIPTION, TaskStatus.IN_PROGRESS);
    }

    public static Task validTaskWithStatus(String userId, TaskStatus status) {
        return task(userId, VALID_TITLE, VALID_DESCRIPTION, status);
    }

    public static Task task(String userId, String title, String description, TaskStatus status) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);

        return task;
    }
}
