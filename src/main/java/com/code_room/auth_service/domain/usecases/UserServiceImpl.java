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
import java.util.Optional;

/**
 * Implementation of the {@link UserService} interface.
 * Handles user-related operations such as finding users by email,
 * verifying passwords, registering users, and verifying user accounts.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * REST client service for communicating with the User API.
     */
    @Autowired
    UserApiService userApiService;

    /**
     * Service responsible for sending emails.
     */
    @Autowired
    private SendEmailService sendEmailService;

    /**
     * Finds a user by their email address by calling an external User API.
     *
     * @param email the email address to search for
     * @return a {@link UserDto} representing the found user
     * @throws IOException          if there is a communication error with the API
     * @throws RuntimeException     if the API response is unsuccessful or the response body is null
     */
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

    /**
     * Checks the user's password by sending login credentials to the external API.
     *
     * @param loginDto the login data transfer object containing email and password
     * @return a {@link UserDto} if credentials are valid
     * @throws IOException          if there is a communication error with the API
     * @throws RuntimeException     if the API response is unsuccessful or the response body is null
     */
    @Override
    public UserDto checkPassword(LoginDto loginDto) throws IOException {
        Response<UserDto> response = userApiService.checkUser(loginDto).execute();

        if (!response.isSuccessful() || response.body() == null) {
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Empty error body";
            throw new RuntimeException("Error calling external API: " + errorMsg);
        }

        return response.body();
    }

    /**
     * Registers a new user by sending user data and password to the external API,
     * then sends a registration success email with a verification code.
     *
     * @param userDto  the user data transfer object containing user information
     * @param password the password for the new user
     * @throws IOException          if there is a communication error with the API
     * @throws RuntimeException     if the API response is unsuccessful or the response body is null
     */
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

    /**
     * Generates a random 6-digit verification code as a String.
     *
     * @return a 6-digit verification code
     */
    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    /**
     * Verifies a user account by sending a verification code to the external API.
     *
     * @param code the verification code to validate the user account
     * @throws IOException          if there is a communication error with the API
     * @throws RuntimeException     if the API response is unsuccessful or the response body is null
     */
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
