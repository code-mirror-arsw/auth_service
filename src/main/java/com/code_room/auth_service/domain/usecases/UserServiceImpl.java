package com.code_room.auth_service.domain.usecases;


import com.code_room.auth_service.domain.ports.SendEmailService;
import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.restclient.UserApiService;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserApiService userApiService;

    @Autowired
    private SendEmailService sendEmailService;

    @Override
    public UserDto findByEmail(String email) throws IOException {
        Response<UserDto> response = userApiService.findByEmail(email)
                .execute();

        if(response.isSuccessful() || response.body() == null){
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Empty error body";
            new RuntimeException("Error calling esternal api " + errorMsg);
        }
        return response.body();
    }

    @Override
    public UserDto checkPassword(LoginDto loginDto) throws IOException {
        Response<UserDto> response = userApiService.checkUser(loginDto).execute();

        if (!response.isSuccessful() || response.body() == null) {
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Empty error body";
            throw new RuntimeException("Error calling external API: " + errorMsg);
        }

        return response.body();
    }


    @Override
    public void registerUser(UserDto userDto, String password) throws IOException {

        Response<Map<String, String>> response = userApiService.createUser(userDto,password)
                .execute();

        if(!response.isSuccessful() || response.body() == null){
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Empty error body";
            throw new RuntimeException("Error calling external API: " + errorMsg);
        }

        String code = response.body().get("verification code");

        sendEmailService.sendRegistrationSuccessEmail(userDto.getEmail(), userDto.getName(), code);
    }

    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    @Override
    public void verifyUser(String code) throws IOException {
        Response<Map<String,String>> response = userApiService.verifyUser(code)
                .execute();

        if(!response.isSuccessful() || response.body() == null){
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Empty error body";
            throw new RuntimeException("Error calling external API: " + errorMsg);
        }

    }
}
