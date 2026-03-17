package korobkin.nikita.task_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.task_service.dto.request.CreateTaskRequest;
import korobkin.nikita.task_service.dto.request.TaskFilterRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskRequest;
import korobkin.nikita.task_service.dto.request.UpdateTaskStatusRequest;
import korobkin.nikita.task_service.dto.response.PagedResponse;
import korobkin.nikita.task_service.dto.response.TaskResponse;
import korobkin.nikita.task_service.exception.ApiError;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@Tag(name = "Task", description = "Create, get, update, delete tasks")
public interface TaskControllerDocs {

    @Operation(
            summary = "Create new task",
            description = "Creates a new task",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task successfully created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "title: Title is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/v1/tasks",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity < TaskResponse > createTask(
            @RequestBody(
                    description = "Create task",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateTaskRequest.class)
                    )
            )
            CreateTaskRequest request,
            Jwt jwt
    );

    @Operation(
            summary = "Get tasks",
            description = "Returns paginated list tasks with optional filters"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    ResponseEntity<PagedResponse<TaskResponse>> getTasks(
            Jwt jwt,
            @ParameterObject TaskFilterRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Get task by id",
            description = "Returns task by its id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this task",
                                              "code": "TASK_ACCESS_DENIED",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Task not found",
                                              "code": "TASK_NOT_FOUND",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            )
    })
    ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Task ID", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,
            Jwt jwt
    );

    @Operation(
            summary = "Update task",
            description = "Updates task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "title: Title is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/v1/tasks",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this task",
                                              "code": "TASK_ACCESS_DENIED",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Task not found",
                                              "code": "TASK_NOT_FOUND",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            )
    })
    ResponseEntity<TaskResponse> updateTaskById(
            @RequestBody(
                    description = "Update task request",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateTaskRequest.class)
                    )
            )
            UpdateTaskRequest request,
            UUID id,
            Jwt jwt
    );

    @Operation(
            summary = "Update task status",
            description = "Updates only task status"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this task",
                                              "code": "TASK_ACCESS_DENIED",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000/status",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Task not found",
                                              "code": "TASK_NOT_FOUND",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000/status",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            )
    })
    ResponseEntity<TaskResponse> updateTaskStatusById(
            @RequestBody(
                    description = "Update task status request",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateTaskStatusRequest.class)
                    )
            )
            UpdateTaskStatusRequest request,
            UUID id,
            Jwt jwt
    );

    @Operation(
            summary = "Delete task",
            description = "Deletes task by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this task",
                                              "code": "TASK_ACCESS_DENIED",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Task not found",
                                              "code": "TASK_NOT_FOUND",
                                              "path": "/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-03-17T00:00:00Z"
                                            }
                                            """)
                    )
            )
    })
    ResponseEntity<Void> deleteTaskById(
            @Parameter(description = "Task ID", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,
            Jwt jwt
    );
}
