package com.code_room.auth_service.config.jwt;

import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.RefreshTokenRequest;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${jwt.signature}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    @Autowired
    private UserService userService;

    public String generateToken(UserDto user) {
        return buildToken(user, expirationMillis);
    }

    public String generateRefreshToken(UserDto user) {
        return buildToken(user, refreshExpirationMillis);
    }

    private String buildToken(UserDto user, long expirationTime) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> buildResponseRefreshToken(RefreshTokenRequest refreshToken) throws IOException {
        if (!isTokenValid(refreshToken.getRefresToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = extractEmail(refreshToken.getRefresToken());
        UserDto user = userService.findByEmail(email);

        String newAccessToken = buildToken(user, expirationMillis);

        String newRefreshToken = generateRefreshToken(user);

        return Map.of(
                "access_token", newAccessToken,
                "refresh_token", newRefreshToken,
                "user_id", user.getIdentification()
        );

    }

    public Map<String,String> buildResponseLogin(UserDto user){
        Map<String,String> response = new HashMap<>();
        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("role", String.valueOf(user.getRole()));
        response.put("email", String.valueOf(user.getEmail()));
        response.put("id", String.valueOf(user.getId()));
        return response;
    }
}