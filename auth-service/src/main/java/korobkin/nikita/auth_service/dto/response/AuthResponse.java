package korobkin.nikita.auth_service.dto.response;

public record AuthResponse(String accessToken, Long expiresIn) {
}
