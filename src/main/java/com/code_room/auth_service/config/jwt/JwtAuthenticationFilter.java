package com.code_room.auth_service.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Global Gateway filter to validate JWT tokens.
 * Skips WebSocket (Upgrade) and any route starting with `/services/be/stream-service/ws`
 */
@Component
@Order(0)
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.signature}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String upgradeHeader = exchange.getRequest().getHeaders().getFirst("Upgrade");
        String path = exchange.getRequest().getURI().getPath();

        if (upgradeHeader != null && upgradeHeader.equalsIgnoreCase("websocket")) {
            return chain.filter(exchange);
        }

        if (path.contains("/stream-service/ws")) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing Authorization header");
        }

        try {
            String token = auth.substring(7);
            byte[] key = Base64.getDecoder().decode(jwtSecret);
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(key))
                    .parseClaimsJws(token)
                    .getBody();

            return chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("user-id", claims.getSubject())
                            .build())
                    .build());

        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid JWT: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        var buf = exchange.getResponse().bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().getHeaders().add("Content-Type", "text/plain");
        return exchange.getResponse().writeWith(Mono.just(buf));
    }
}
