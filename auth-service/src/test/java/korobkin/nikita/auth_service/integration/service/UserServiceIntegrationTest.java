package korobkin.nikita.auth_service.integration.service;

import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.exception.UserAlreadyExistsException;
import korobkin.nikita.auth_service.exception.UserNotFoundException;
import korobkin.nikita.auth_service.fixtures.UserRepresentationFixtures;
import korobkin.nikita.auth_service.integration.AbstractDbIntegrationTest;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest extends AbstractDbIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUserFromKeycloak_shouldSaveUser() {
        UserRepresentation keycloakUser = UserRepresentationFixtures.user();

        userService.saveUserFromKeycloak(keycloakUser);

        User saved = userRepository.findByKeycloakId(UserRepresentationFixtures.ID).orElseThrow();
        assertEquals(UserRepresentationFixtures.USERNAME, saved.getUsername());
        assertEquals(UserRepresentationFixtures.EMAIL, saved.getEmail());
    }

    @Test
    void saveUserFromKeycloak_withExistingUser_shouldThrow() {
        UserRepresentation keycloakUser = UserRepresentationFixtures.user();
        userService.saveUserFromKeycloak(keycloakUser);

        UserRepresentation duplicate = UserRepresentationFixtures.user(
                UserRepresentationFixtures.ID,
                "anotheruser",
                "another@example.com"
        );

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> userService.saveUserFromKeycloak(duplicate));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void getUserById_shouldReturnUserResponse() {
        UserRepresentation keycloakUser = UserRepresentationFixtures.user();
        userService.saveUserFromKeycloak(keycloakUser);

        var response = userService.getUserById(UserRepresentationFixtures.ID);

        assertEquals(UserRepresentationFixtures.USERNAME, response.username());
        assertEquals(UserRepresentationFixtures.EMAIL, response.email());
        assertNotNull(response.id());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
    }

    @Test
    void getUserById_withNonExistentId_shouldThrow() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById("non-existent-id"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}
