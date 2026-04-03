package korobkin.nikita.auth_service.fixtures;

import lombok.experimental.UtilityClass;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@UtilityClass
public class UserRepresentationFixtures {

    public static final String ID = "kc-id-1";
    public static final String USERNAME = "testuser";
    public static final String EMAIL = "test@example.com";
    public static final String PASSWORD = "password123";

    public static UserRepresentation user() {
        return user(ID, USERNAME, EMAIL);
    }

    public static UserRepresentation user(String id, String username, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(PASSWORD);
        user.setCredentials(List.of(credential));

        return user;
    }
}
