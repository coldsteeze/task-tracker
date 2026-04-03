package korobkin.nikita.auth_service.service;

import korobkin.nikita.auth_service.dto.response.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;

public interface UserService {

    void saveUserFromKeycloak(UserRepresentation keycloakUser);

    UserResponse getUserById(String id);
}
