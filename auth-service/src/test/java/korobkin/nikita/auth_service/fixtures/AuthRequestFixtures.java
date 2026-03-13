package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthRequestFixtures {

    public static final String VALID_USERNAME = "username";
    public static final String VALID_EMAIL = "email@mail.ru";
    public static final String VALID_PASSWORD = "password";

    public static final String INVALID_USERNAME = "use";

    public static final String USERNAME_EXISTS_USER = "existing-user";
    public static final String PASSWORD_EXISTS_USER = "password";

    public static RegisterRequest validRegisterRequest() {
        return registerRequest(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
    }

    public static RegisterRequest invalidRegisterRequest() {
        return registerRequest(INVALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
    }

    public static RegisterRequest registerRequestWithUsername(String username) {
        return registerRequest(username, VALID_EMAIL, VALID_PASSWORD);
    }

    public static RegisterRequest registerRequest(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    public static LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return request;
    }
}
