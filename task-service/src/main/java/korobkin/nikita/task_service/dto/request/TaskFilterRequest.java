package korobkin.nikita.task_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.task_service.entity.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request filter DTO for searching user's projects")
public class TaskFilterRequest {

    @Schema(example = "portfolio", description = "Partial search in project name")
    private String search;

    @Schema(example = "IN_PROGRESS", description = "Status of task (e.g. TODO, DONE, IN_PROGRESS)")
    private TaskStatus status;

    @Schema(description = "Filter by creation date (start, optional)")
    private LocalDateTime createdAfter;

    @Schema(description = "Filter by creation date (end, optional)")
    private LocalDateTime createdBefore;

    @Schema(description = "Filter by updated date (start, optional)")
    private LocalDateTime updatedAfter;

    @Schema(description = "Filter by updated date (end, optional)")
    private LocalDateTime updatedBefore;
}
