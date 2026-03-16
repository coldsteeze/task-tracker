package korobkin.nikita.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User login request")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username too long")
    @Schema(example = "example@mail.ru", description = "Valid email address")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(example = "password", description = "User password (minimum 8 characters)")
    private String password;
}
