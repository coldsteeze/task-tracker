package korobkin.nikita.auth_service.service;

import korobkin.nikita.auth_service.dto.request.RegisterRequest;

public interface KeycloakService {

    void registerUser(RegisterRequest request);
}
