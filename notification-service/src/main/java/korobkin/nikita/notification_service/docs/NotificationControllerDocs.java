package korobkin.nikita.notification_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.notification_service.dto.NotificationFilterRequest;
import korobkin.nikita.notification_service.dto.NotificationResponse;
import korobkin.nikita.notification_service.dto.PagedResponse;
import korobkin.nikita.notification_service.exception.ApiError;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Notification", description = "Get, update notifications")
public interface NotificationControllerDocs {

    @Operation(
            summary = "Get notifications",
            description = "Returns paginated list notifications with optional filters"
    )
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    ResponseEntity<PagedResponse<NotificationResponse>> getNotifications(
            @ParameterObject NotificationFilterRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Read notification by id",
            description = "Read notification and return information about it"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification read"),
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
                                              "message": "You do not have permission to access this notification",
                                              "code": "NOTIFICATION_ACCESS_DENIED",
                                              "path": "/api/v1/notifications/550e8400-e29b-41d4-a716-446655440000/read",
                                              "timestamp": "2026-03-20T00:00:00Z"
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
                                              "message": "Notification not found",
                                              "code": "NOTIFICATION_NOT_FOUND",
                                              "path": "/api/v1/notifications/550e8400-e29b-41d4-a716-446655440000/read",
                                              "timestamp": "2026-03-20T00:00:00Z"
                                            }
                                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Notification with this id already read",
                                              "code": "NOTIFICATION_ALREADY_READ",
                                              "path": "/api/v1/notifications/550e8400-e29b-41d4-a716-446655440000/read",
                                              "timestamp": "2026-03-20T00:00:00Z"
                                            }
                                            """)
                    )
            )

    })
    ResponseEntity<NotificationResponse> readNotification(
            @Parameter(description = "Notification ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID id
    );
}
