package com.code_room.auth_service.infrastructure.restclient.config;


import com.code_room.auth_service.infrastructure.restclient.UserApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {

  @Value("${spring.application.restclient.user.url}")
  private String UserUrl;


  private static final long TIMEOUT_SECONDS = 60;

  @Bean
  @Qualifier("userRetrofit")
  public Retrofit userRetrofit() {
    return new Retrofit.Builder()
            .baseUrl(UserUrl)
            .client(new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build())
            .addConverterFactory(JacksonConverterFactory.create(
                    new ObjectMapper()
                            .findAndRegisterModules()
            ))

            .build();
  }

  @Bean
  public static UserApiService getUserApiService(@Qualifier("userRetrofit")Retrofit userRetrofit) {
    return userRetrofit.create(UserApiService.class);
  }

}
