package com.code_room.auth_service.infrastructure.restclient;


import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;



/**
 * Retrofit interface defining the REST client methods to interact with the external User API.
 *
 * <p>Provides operations to create users, find users by email, verify users, and check user credentials.
 */
public interface UserApiService {

    /**
     * Creates a new user with the provided user details and password.
     *
     * @param user the user data transfer object containing user information
     * @param password the password for the new user
     * @return a Call object wrapping a Map containing response data (e.g., verification code)
     */
    @POST("users/create")
    Call<Map<String, String>> createUser(@Body UserDto user, @Query("password") String password);

    /**
     * Retrieves user details by their email address.
     *
     * @param email the email address of the user to find
     * @return a Call object wrapping the UserDto if found
     */
    @GET("users/email/{email}")
    Call<UserDto> findByEmail(@Path("email") String email);

    /**
     * Verifies a user's account using a verification code.
     *
     * @param code the verification code sent to the user
     * @return a Call object wrapping a Map with verification result information
     */
    @GET("users/verify")
    Call<Map<String, String>> verifyUser(@Query("code") String code);

    /**
     * Checks user credentials for authentication.
     *
     * @param loginDto the login data transfer object containing user credentials (email and password)
     * @return a Call object wrapping the UserDto if the credentials are valid
     */
    @POST("users/user-check")
    Call<UserDto> checkUser(@Body LoginDto loginDto);

}
