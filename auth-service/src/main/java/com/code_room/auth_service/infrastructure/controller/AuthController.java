package com.code_room.auth_service.infrastructure.controller;
import com.code_room.auth_service.domain.Exception.LoginException;
import com.code_room.auth_service.config.jwt.JwtService;
import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.controller.dto.RefreshTokenRequest;
import com.code_room.auth_service.infrastructure.controller.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            User user = userService.checkPassword(loginDto);
            return ResponseEntity.ok(jwtService.buildResponseLogin(user));

        }catch (LoginException e){
            Map<String, String> error = Map.of("message", e.getMessage(),"code","405");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch (Exception e) {
            Map<String, String> error = Map.of("message", "Invalid email or password","code","403");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

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

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        try {
            userService.verifyUser(code);
            return ResponseEntity.ok("Your account has been successfully verified. You can now log in.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            Map<String, Object> response = jwtService.buildResponseRefreshToken(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", e.getMessage(), "code", "401")
            );
        }
    }





}
