package korobkin.nikita.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User response containing information about user")
public record UserResponse(

        @Schema(description = "User id",
                example = "973a43fe-fa06-4565-88ec-c54db069e349")
        String id,

        @Schema(description = "Keycloak id",
                example = "9c81dfd9-d481-41dc-aae1-b558f25bab94")
        String keycloakId,

        @Schema(description = "Username",
                example = "username")
        String username,

        @Schema(description = "Email",
                example = "example@mail.ru")
        String email,

        @Schema(description = "Created at",
                example = "2026-03-12 01:14:58.976637")
        LocalDateTime createdAt
) {}
