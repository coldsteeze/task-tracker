package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFixtures {

    public static User user(String keycloakId, String username, String email) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(username);
        user.setEmail(email);

        return user;
    }
}
