package com.code_room.auth_service.domain.mapper.userMapper;
import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.infrastructure.controller.dto.UserDto;
import com.code_room.auth_service.infrastructure.repository.entities.UserEntity;

public interface UserMapper {
    UserDto toDtoFromModel(User user);

    User toModelFromDto(UserDto dto);

    UserDto toDtoFromEntity(UserEntity entity);

    UserEntity toEntityFromDto(UserDto dto);

    User toModelFromEntity(UserEntity entity);

    UserEntity toEntityFromModel(User user);
}

