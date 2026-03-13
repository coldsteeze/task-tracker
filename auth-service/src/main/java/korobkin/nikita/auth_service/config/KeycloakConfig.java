package korobkin.nikita.auth_service.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak adminKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealmResource())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakProperties.getAdminClient().getClientId())
                .clientSecret(keycloakProperties.getAdminClient().getClientSecret())
                .build();
    }

    @Bean
    public RealmResource adminRealmResource(Keycloak adminKeycloak) {
        return adminKeycloak.realm(keycloakProperties.getRealmResource());
    }
}
