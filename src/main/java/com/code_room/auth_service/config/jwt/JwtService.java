package com.code_room.auth_service.config.jwt;

import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.RefreshTokenRequest;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for generating and validating JWT tokens.
 *
 * <p>Handles token creation for access and refresh tokens, extracting claims,
 * validating tokens, and building response payloads for login and token refresh operations.
 */
@Component
public class JwtService {

    @Value("${jwt.signature}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    @Autowired
    UserService userService;

    /**
     * Decodes the base64-encoded secret key for signing JWTs.
     *
     * @return byte array representing the secret key
     */
    private byte[] getSecretKey() {
        return Base64.getDecoder().decode(secretKeyBase64);
    }

    /**
     * Generates a JWT access token for the specified user.
     *
     * @param user the user data transfer object
     * @return a signed JWT access token as a String
     */
    public String generateToken(UserDto user) {
        return buildToken(user, expirationMillis);
    }

    /**
     * Generates a JWT refresh token for the specified user.
     *
     * @param user the user data transfer object
     * @return a signed JWT refresh token as a String
     */
    public String generateRefreshToken(UserDto user) {
        return buildToken(user, refreshExpirationMillis);
    }

    /**
     * Builds a JWT token with user details and expiration time.
     *
     * @param user the user data transfer object
     * @param expirationTime token expiration time in milliseconds
     * @return a signed JWT token as a String
     */
    private String buildToken(UserDto user, long expirationTime) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(getSecretKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token as a String
     * @return the claims contained in the token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(getSecretKey()))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the email (subject) from the JWT token.
     *
     * @param token the JWT token as a String
     * @return the email contained in the token subject
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the user role from the JWT token claims.
     *
     * @param token the JWT token as a String
     * @return the role contained in the token claims
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validates whether the JWT token is properly signed and not expired.
     *
     * @param token the JWT token as a String
     * @return true if the token is valid; false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Builds a response map containing new access and refresh tokens based on a valid refresh token.
     *
     * @param refreshToken the refresh token request containing the refresh token string
     * @return a map with new access_token, refresh_token, and user_id
     * @throws IOException if user lookup fails
     * @throws RuntimeException if the refresh token is invalid
     */
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

    /**
     * Builds a response map containing access and refresh tokens along with user details after login.
     *
     * @param user the authenticated user DTO
     * @return a map containing accessToken, refreshToken, role, email, and id
     */
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
