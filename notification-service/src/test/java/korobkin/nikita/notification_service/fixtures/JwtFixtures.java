package korobkin.nikita.notification_service.fixtures;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@UtilityClass
public class JwtFixtures {

    public static Jwt createJwtWithUserId(String userId) {
        return new Jwt(
                "fake-token",
                null,
                null,
                Map.of("alg", "none"),
                Map.of("sub", userId)
        );
    }
}
