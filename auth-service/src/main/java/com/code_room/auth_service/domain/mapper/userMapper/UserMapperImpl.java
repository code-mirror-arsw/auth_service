package com.code_room.auth_service.domain.mapper.userMapper;

import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.infrastructure.controller.dto.UserDto;
import com.code_room.auth_service.infrastructure.repository.entities.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDtoFromModel(User user) {
        return UserDto.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .identification(user.getIdentification())
                .role(user.getRole())
                .build();
    }

    @Override
    public User toModelFromDto(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .identification(dto.getIdentification())
                .role(dto.getRole())
                .build();
    }

    @Override
    public UserDto toDtoFromEntity(UserEntity entity) {
        return UserDto.builder()
                .name(entity.getName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .identification(entity.getIdentification())
                .role(entity.getRole())
                .build();
    }

    @Override
    public UserEntity toEntityFromDto(UserDto dto) {
        return UserEntity.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .identification(dto.getIdentification())
                .role(dto.getRole())
                .build();
    }

    @Override
    public User toModelFromEntity(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .identification(entity.getIdentification())
                .role(entity.getRole())
                .build();
    }

    @Override
    public UserEntity toEntityFromModel(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .identification(user.getIdentification())
                .role(user.getRole())
                .build();
    }
}
