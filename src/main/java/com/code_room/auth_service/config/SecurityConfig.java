package com.code_room.auth_service.config;

import com.code_room.auth_service.config.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration class for setting up Spring Security in a WebFlux application.
 *
 * This class disables CSRF, HTTP Basic authentication, and form login.
 * It configures all requests to be permitted without authentication.
 * The JWT filter is injected but not currently used in the security chain.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * JWT filter responsible for processing JWT tokens in requests.
     */
    private final JwtFilter jwtFilter;

    /**
     * Constructs the SecurityConfig with the provided JWT filter.
     *
     * @param jwtFilter the JWT filter to be used in the security chain
     */
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * <p>Current configuration:
     * <ul>
     *   <li>Disables CSRF protection</li>
     *   <li>Permits all requests without authentication</li>
     *   <li>Disables HTTP Basic authentication</li>
     *   <li>Disables form login</li>
     * </ul>
     *
     * @param http the {@link ServerHttpSecurity} to configure
     * @return the configured {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
