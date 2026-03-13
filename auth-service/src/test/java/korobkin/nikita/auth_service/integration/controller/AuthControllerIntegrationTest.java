package korobkin.nikita.auth_service.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.integration.AbstractAuthIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AuthControllerIntegrationTest extends AbstractAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnCreated() throws Exception {
        RegisterRequest request = AuthRequestFixtures.validRegisterRequest();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_withInvalidName_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = AuthRequestFixtures.invalidRegisterRequest();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_withExistsName_shouldReturnConflict() throws Exception {
        RegisterRequest request = AuthRequestFixtures.registerRequestWithUsername(
                AuthRequestFixtures.USERNAME_EXISTS_USER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_ALREADY_EXISTS.message));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        LoginRequest request = AuthRequestFixtures.loginRequest(
            AuthRequestFixtures.USERNAME_EXISTS_USER,
            AuthRequestFixtures.PASSWORD_EXISTS_USER
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    void login_withBlankPassword_shouldReturnBadRequest() throws Exception {
        LoginRequest request = AuthRequestFixtures.loginRequest(
                AuthRequestFixtures.USERNAME_EXISTS_USER,
                ""
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        LoginRequest request = AuthRequestFixtures.loginRequest(
                AuthRequestFixtures.USERNAME_EXISTS_USER,
                "password1"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CREDENTIALS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_CREDENTIALS.message));
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}
