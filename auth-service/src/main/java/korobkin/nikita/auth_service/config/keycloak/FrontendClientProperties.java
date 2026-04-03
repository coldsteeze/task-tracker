package korobkin.nikita.auth_service.config.keycloak;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontendClientProperties {

    private String clientId;
    private String clientSecret;
}
