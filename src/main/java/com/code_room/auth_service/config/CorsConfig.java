package com.code_room.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration class to set up CORS (Cross-Origin Resource Sharing) for the application.
 *
 * <p>This configuration allows requests from specific origins and permits all HTTP methods and headers.
 * It also enables credentials support.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a {@link CorsWebFilter} bean that configures CORS settings for the application.
     *
     * <p>Allowed origins:
     * <ul>
     *   <li>http://localhost:5173</li>
     *   <li>http://localhost:5000</li>
     * </ul>
     *
     * <p>Allowed HTTP methods and headers are unrestricted.
     * Credentials (cookies, authorization headers) are allowed.
     *
     * @return a configured {@link CorsWebFilter} instance
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:5173");
        corsConfig.addAllowedOrigin("https://victorious-water-0ec758310.2.azurestaticapps.net");

        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}

