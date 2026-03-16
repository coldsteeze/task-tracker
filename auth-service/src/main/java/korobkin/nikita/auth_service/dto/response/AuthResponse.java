package korobkin.nikita.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response containing access and expires in")
public record AuthResponse(

        @Schema(description = "Access token for authenticated requests",
                example = "Bearer: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Minute expiration time of the access token",
                example = "15")
        Long expiresIn) {
}
