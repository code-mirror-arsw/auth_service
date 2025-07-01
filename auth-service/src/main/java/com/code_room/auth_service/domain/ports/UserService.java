package com.code_room.auth_service.domain.ports;

import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;

import java.io.IOException;

public interface UserService {
    UserDto findByEmail(String email) throws IOException;

    UserDto checkPassword(LoginDto login) throws IOException;

    void registerUser(UserDto userDto, String password) throws IOException;

    void verifyUser(String code) throws IOException;
}
