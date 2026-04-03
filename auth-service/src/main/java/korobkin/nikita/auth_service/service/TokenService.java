package korobkin.nikita.auth_service.service;

import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.response.AuthResponse;

public interface TokenService {

    AuthResponse login(LoginRequest request);
}
