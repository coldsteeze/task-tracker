package korobkin.nikita.auth_service.service.impl;

import jakarta.ws.rs.core.Response;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.exception.KeycloakRegistrationFailedException;
import korobkin.nikita.auth_service.exception.UserAlreadyExistsException;
import korobkin.nikita.auth_service.mapper.KeycloakMapper;
import korobkin.nikita.auth_service.service.KeycloakService;
import korobkin.nikita.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final RealmResource adminRealmResource;
    private final KeycloakMapper keycloakMapper;
    private final UserService userService;

    @Override
    public void registerUser(RegisterRequest request) {
        UserRepresentation user = buildUserRepresentation(request);

        String userId = createKeycloakUser(user);

        UserRepresentation createdUser = keycloakMapper.toUserRepresentation(request);
        createdUser.setId(userId);

        userService.saveUserFromKeycloak(createdUser);

        log.info("User successfully registered username={} id={}",
                request.getUsername(), userId);
    }

    private UserRepresentation buildUserRepresentation(RegisterRequest request) {
        UserRepresentation user = keycloakMapper.toUserRepresentation(request);

        user.setEnabled(true);
        user.setRequiredActions(Collections.emptyList());

        CredentialRepresentation credential =
                keycloakMapper.toCredentialRepresentation(request);

        user.setCredentials(List.of(credential));

        return user;
    }

    private String createKeycloakUser(UserRepresentation user) {
        try (Response response = adminRealmResource.users().create(user)) {
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new UserAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS);
            }

            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                String body = response.readEntity(String.class);

                log.error("Failed to create user in Keycloak. Status={}, body={}",
                        response.getStatus(), body);

                throw new KeycloakRegistrationFailedException(ErrorCode.KEYCLOAK_REGISTRATION_FAILED);
            }

            return CreatedResponseUtil.getCreatedId(response);
        }
    }
}
