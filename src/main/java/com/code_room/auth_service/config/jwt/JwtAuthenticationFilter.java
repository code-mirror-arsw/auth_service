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

@Component
@Order(0)
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.signature}")
    private String jwtSecretBase64;

    @Override
    public Mono<Void> filter(ServerWebExchange ex, GatewayFilterChain chain) {

        String path = ex.getRequest().getURI().getPath();

        if ("websocket".equalsIgnoreCase(ex.getRequest().getHeaders().getFirst("Upgrade")))
            return chain.filter(ex);

        if (path.startsWith("/services/be/stream-service/ws"))
            return chain.filter(ex);

        String auth = ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer "))
            return unauthorized(ex, "Missing Authorization header");

        try {
            String token = auth.substring(7);
            byte[] key = Base64.getDecoder().decode(jwtSecretBase64);

            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(key))
                    .parseClaimsJws(token)
                    .getBody();

            return chain.filter(ex.mutate()
                    .request(ex.getRequest().mutate()
                            .header("user-id", claims.getSubject())
                            .build())
                    .build());

        } catch (JwtException e) {
            return unauthorized(ex, "Invalid JWT: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange ex, String msg){
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        var buf = ex.getResponse().bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));
        ex.getResponse().getHeaders().add("Content-Type", "text/plain");
        return ex.getResponse().writeWith(Mono.just(buf));
    }
}
