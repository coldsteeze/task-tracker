package korobkin.nikita.auth_service.service.impl;

import korobkin.nikita.auth_service.dto.response.UserResponse;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.exception.UserAlreadyExistsException;
import korobkin.nikita.auth_service.exception.UserNotFoundException;
import korobkin.nikita.auth_service.mapper.UserMapper;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void saveUserFromKeycloak(UserRepresentation keycloakUser) {
        if (userRepository.findByKeycloakId(keycloakUser.getId()).isPresent()) {
            throw new UserAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS);
        }

        userRepository.save(userMapper.toUser(keycloakUser));
        log.info("User with keycloak id {} is successfully saved", keycloakUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findByKeycloakId(id)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        log.info("Get information about user with keycloak id {}", id);

        return userMapper.toUserResponse(user);
    }
}

