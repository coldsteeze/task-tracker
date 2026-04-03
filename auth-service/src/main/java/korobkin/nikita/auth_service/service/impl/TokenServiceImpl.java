package korobkin.nikita.auth_service.service.impl;

import korobkin.nikita.auth_service.config.KeycloakProperties;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.response.AuthResponse;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final KeycloakProperties keycloakProperties;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try (Keycloak keycloak = buildKeycloak(loginRequest)) {
            return toAuthResponse(keycloak.tokenManager().getAccessToken(), loginRequest);
        } catch (Exception e) {
            log.warn("Failed to obtain token for username={}", loginRequest.getUsername(), e);
            throw new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    private Keycloak buildKeycloak(LoginRequest loginRequest) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealmResource())
                .clientId(keycloakProperties.getFrontendClient().getClientId())
                .clientSecret(keycloakProperties.getFrontendClient().getClientSecret())
                .grantType(OAuth2Constants.PASSWORD)
                .username(loginRequest.getUsername())
                .password(loginRequest.getPassword())
                .build();
    }

    private AuthResponse toAuthResponse(AccessTokenResponse tokenResponse, LoginRequest request) {
        String tokenString = "Bearer " + tokenResponse.getToken();
        Long expiresIn = tokenResponse.getExpiresIn();

        log.info("Token obtained for username={}", request.getUsername());
        return new AuthResponse(tokenString, expiresIn);
    }
}
