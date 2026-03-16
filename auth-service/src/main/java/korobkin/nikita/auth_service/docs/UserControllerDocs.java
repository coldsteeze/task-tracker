package korobkin.nikita.auth_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.auth_service.dto.response.UserResponse;
import korobkin.nikita.auth_service.exception.ApiError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "User", description = "Manage users: get")
public interface UserControllerDocs {

    @Operation(
            summary = "Get information about user",
            description = "Return information about user by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully fetched",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User not found",
                                              "code": "USER_NOT_FOUND",
                                              "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-03-16T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserResponse> getUserById(
            @PathVariable @Parameter(
                    description = "User ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            ) String userId);
}
