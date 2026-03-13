package korobkin.nikita.auth_service.integration.controller;

import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.UserFixtures;
import korobkin.nikita.auth_service.integration.AbstractDbIntegrationTest;
import korobkin.nikita.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest extends AbstractDbIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = UserFixtures.user(
                "keycloak_id",
                AuthRequestFixtures.VALID_USERNAME,
                AuthRequestFixtures.VALID_EMAIL
        );

        userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/" + user.getKeycloakId())
                        .with(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.keycloakId").value(user.getKeycloakId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt().toString()));
    }

    @Test
    void getUserById_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + UUID.randomUUID())
                        .with(auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message));
    }

    private RequestPostProcessor auth() {
        return authentication(new UsernamePasswordAuthenticationToken(
                "",
                null,
                Collections.emptyList()
        ));
    }
}
