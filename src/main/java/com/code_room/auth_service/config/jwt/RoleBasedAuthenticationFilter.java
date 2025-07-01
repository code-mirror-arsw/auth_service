package com.code_room.auth_service.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RoleBasedAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.signature}")
    private String jwtSecret;

    @Autowired
    private RoleAccessConfig roleAccessConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header.");
        }

        try {
            String token = authHeader.replace("Bearer ", "");
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret)))
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            String userId = claims.getSubject();

            if (!isAuthorized(role, path)) {
                return unauthorized(exchange, "Unauthorized access for role: " + role);
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("user-id", userId)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid JWT token: " + e.getMessage());
        }
    }

    private boolean isAuthorized(String role, String path) {
        if (role == null || !roleAccessConfig.getAccess().containsKey(role)) return false;
        return roleAccessConfig.getAccess().get(role)
                .stream().anyMatch(routePattern -> pathMatches(path, routePattern));
    }

    private boolean pathMatches(String path, String pattern) {
        String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
        return path.matches(regex);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().add("Content-Type", "text/plain");
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
