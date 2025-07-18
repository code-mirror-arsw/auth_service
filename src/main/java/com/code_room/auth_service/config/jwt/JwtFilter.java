package com.code_room.auth_service.config.jwt;

import com.code_room.auth_service.domain.ports.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * JWT filter that intercepts HTTP requests to authenticate users based on JWT tokens.
 *
 * <p>This filter extracts the JWT token from the Authorization header, validates it,
 * fetches the user details, and sets the security context for downstream handlers.
 */
@Component
public class JwtFilter implements WebFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    /**
     * Filters incoming requests and attempts to authenticate the user if a valid JWT token is present.
     *
     * @param exchange the current server exchange containing the request and response
     * @param chain the web filter chain to delegate to the next filter
     * @return a Mono that completes when request processing is done
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email == null) {
            return chain.filter(exchange);
        }

        return Mono.fromCallable(() -> userService.findByEmail(email))
                .filter(user -> user != null && jwtService.isTokenValid(token))
                .map(user -> {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                    );
                    return new SecurityContextImpl(authToken);
                })
                .flatMap(ctx -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(ctx))))
                .switchIfEmpty(chain.filter(exchange));
    }
}
