package com.code_room.auth_service.infrastructure.restclient;


import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface UserApiService {
    @POST("users/create")
    Call<Map<String,String>> createUser(@Body UserDto user, @Query("password") String password);

    @GET("users/email/{email}")
    Call<UserDto> findByEmail(@Path("email") String email);

    @GET("users//verify/")
    Call<String> verifyUser(@Path("code") String code);

    @POST("users/user-check")
    Call<UserDto> checkUser(@Body LoginDto loginDto);


}
