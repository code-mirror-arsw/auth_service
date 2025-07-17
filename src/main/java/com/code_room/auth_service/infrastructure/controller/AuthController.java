package com.code_room.auth_service.infrastructure.controller;

import com.code_room.auth_service.domain.Exception.LoginException;
import com.code_room.auth_service.config.jwt.JwtService;
import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.controller.dto.RefreshTokenRequest;
import com.code_room.auth_service.infrastructure.restclient.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller that manages authentication-related endpoints.
 *
 * <p>Supports user login, registration, account verification, and JWT token refresh.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Authenticates a user by validating their credentials.
     *
     * @param loginDto the login data transfer object containing email and password
     * @return a ResponseEntity containing JWT tokens on success or an error message on failure
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            UserDto user = userService.checkPassword(loginDto);
            return ResponseEntity.ok(jwtService.buildResponseLogin(user));

        } catch (LoginException e) {
            Map<String, String> error = Map.of("message", e.getMessage(), "code", "405");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = Map.of("message", e.getMessage(), "code", "403");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Registers a new user with the provided details and password.
     *
     * @param dto the user data transfer object containing user details
     * @param password the password for the new user
     * @return a ResponseEntity with a success message or error details
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto, @RequestParam String password) {
        try {
            userService.registerUser(dto, password);
            Map<String, String> success = Map.of(
                    "message", "User registered successfully"
            );
            return ResponseEntity.ok(success);

        } catch (Exception e) {
            Map<String, String> error = Map.of(
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Verifies a user's account using the provided verification code.
     *
     * @param code the verification code sent to the user
     * @return a ResponseEntity with a success message or error message if verification fails
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        try {
            userService.verifyUser(code);
            return ResponseEntity.ok("Your account has been successfully verified. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Verification failed: " + e.getMessage());
        }
    }

    /**
     * Refreshes the JWT access and refresh tokens using a valid refresh token.
     *
     * @param request the refresh token request containing the refresh token
     * @return a ResponseEntity with new JWT tokens or an error message if unauthorized
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            Map<String, Object> response = jwtService.buildResponseRefreshToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", e.getMessage(), "code", "401")
            );
        }
    }
}
