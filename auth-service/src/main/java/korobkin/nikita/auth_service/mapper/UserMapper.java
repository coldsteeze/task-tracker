package korobkin.nikita.auth_service.mapper;

import korobkin.nikita.auth_service.dto.response.UserResponse;
import korobkin.nikita.auth_service.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", source = "id")
    User toUser(UserRepresentation userRepresentation);

    UserResponse toUserResponse(User user);
}
