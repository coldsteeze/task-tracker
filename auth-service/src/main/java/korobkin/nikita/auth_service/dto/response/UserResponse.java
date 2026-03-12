package korobkin.nikita.auth_service.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String keycloakId,
        String username,
        String email,
        LocalDateTime createdAt
) {}
