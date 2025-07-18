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

/**
 * Configuration class for setting up Retrofit clients.
 * <p>
 * Provides configuration and beans necessary to create Retrofit instances for REST API calls.
 * Specifically configures a Retrofit client to communicate with the User service.
 * </p>
 */
@Configuration
public class RetrofitConfig {

  /**
   * Base URL for the User API service.
   * Injected from application properties using the key {@code spring.application.restclient.user.url}.
   */
  @Value("${spring.application.restclient.user.url}")
  private String UserUrl;

  /**
   * Timeout duration in seconds for network calls.
   */
  private static final long TIMEOUT_SECONDS = 60;

  /**
   * Creates and configures a Retrofit instance for the User API service.
   *
   * @return configured Retrofit instance qualified with "userRetrofit"
   */
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

  /**
   * Creates a UserApiService bean backed by the configured Retrofit client.
   *
   * @param userRetrofit the Retrofit instance qualified with "userRetrofit"
   * @return the UserApiService proxy instance
   */
  @Bean
  public static UserApiService getUserApiService(@Qualifier("userRetrofit") Retrofit userRetrofit) {
    return userRetrofit.create(UserApiService.class);
  }
}
