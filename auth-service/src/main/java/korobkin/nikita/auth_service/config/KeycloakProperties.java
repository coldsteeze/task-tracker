package korobkin.nikita.auth_service.config;

import korobkin.nikita.auth_service.config.keycloak.AdminClientProperties;
import korobkin.nikita.auth_service.config.keycloak.FrontendClientProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {

    private String authServerUrl;
    private String realmResource;

    private AdminClientProperties adminClient;
    private FrontendClientProperties frontendClient;
}
