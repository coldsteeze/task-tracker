package korobkin.nikita.auth_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.AuthResponse;
import korobkin.nikita.auth_service.exception.ApiError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Registration, login")
public interface AuthControllerDocs {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered"
                    ),
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
                                              "message": "email: Email is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/v1/auth/register",
                                              "timestamp": "2026-03-16T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "User already exists",
                                              "code": "USER_ALREADY_EXISTS",
                                              "path": "/api/v1/auth/register",
                                              "timestamp": "2026-03-16T00:00:00Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Void> register(@RequestBody(
            description = "Registration data",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RegisterRequest.class)
            )
    ) RegisterRequest registerRequest);

    @Operation(
            summary = "User login",
            description = "Returns access token in response body",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully login"
                    ),
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
                                              "message": "password: Password is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/v1/auth/login",
                                              "timestamp": "2026-03-16T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                    responseCode = "401",
                    description = "Invalid login credentials",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid username or password",
                                              "code": "INVALID_CREDENTIALS",
                                              "path": "/api/v1/auth/login",
                                              "timestamp": "2026-03-16T00:00:00Z"
                                            }
                                            """)
                    )
            )
            }
    )
    ResponseEntity<AuthResponse> login(@RequestBody(
            description = "Login data",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginRequest.class)
            )
    ) LoginRequest loginRequest);
}
