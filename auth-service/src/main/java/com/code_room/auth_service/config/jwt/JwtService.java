package com.code_room.auth_service.config.jwt;

import com.code_room.auth_service.domain.mapper.userMapper.UserMapper;
import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.RefreshTokenRequest;
import com.code_room.auth_service.infrastructure.repository.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public String generateToken(User user) {
        return buildToken(user, expirationMillis);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMillis);
    }

    private String buildToken(User user, long expirationTime) {
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

    public Map<String, Object> buildResponseRefreshToken(RefreshTokenRequest refreshToken) {
        if (!isTokenValid(refreshToken.getRefresToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = extractEmail(refreshToken.getRefresToken());
        User user = userService.findByEmail(email);

        String newAccessToken = buildToken(user, expirationMillis);

        String newRefreshToken = generateRefreshToken(user);

        return Map.of(
                "access_token", newAccessToken,
                "refresh_token", newRefreshToken,
                "user_id", user.getIdentification()
        );

    }

    public Map<String,String> buildResponseLogin(User user){
        Map<String,String> response = new HashMap<>();
        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", String.valueOf(user.getId()));
        return response;
    }
}
