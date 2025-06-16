package com.code_room.auth_service.domain.ports;

import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.controller.dto.UserDto;
import com.code_room.auth_service.infrastructure.repository.entities.UserEntity;

public interface UserService {
    User findByEmail(String email);

    User checkPassword(LoginDto login);

    void registerUser(UserDto userDto, String password);

    void verifyUser(String code);
}
