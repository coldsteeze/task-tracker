package korobkin.nikita.auth_service.mapper;

import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KeycloakMapper {

    @Mapping(target = "enabled", constant = "true")
    UserRepresentation toUserRepresentation(RegisterRequest registerRequest);

    @Mapping(target = "type", constant = "password")
    @Mapping(target = "temporary", constant = "false")
    @Mapping(target = "value", source = "password")
    CredentialRepresentation toCredentialRepresentation(RegisterRequest registerRequest);
}
