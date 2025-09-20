package com.jwt.authentication.controller;

import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.dto.RefreshTokenRequest;
import com.jwt.authentication.dto.RegisterDto;
import com.jwt.authentication.dto.ChangePasswordDto;
import com.jwt.authentication.dto.UpdateProfileDto;
import com.jwt.authentication.dto.UserProfileResponse;
import com.jwt.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authenticate(@Valid @RequestBody LoginDto loginDto) {

        logger.info("Login request received for username/email: {}", loginDto.getUsernameOrEmail());
        JWTAuthResponse jwtAuthResponse = authService.login(loginDto);

        logger.info("Login successful for username/email: {}", loginDto.getUsernameOrEmail());
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<JWTAuthResponse> register(@Valid @RequestBody RegisterDto registerDto) {

        logger.info("Registration request received for username: {}", registerDto.getUsername());
        JWTAuthResponse jwtAuthResponse = authService.register(registerDto);

        logger.info("Registration successful for username: {}", registerDto.getUsername());
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        logger.info("Refresh token request received");
        JWTAuthResponse jwtAuthResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());

        logger.info("Token refreshed successfully");
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {

        logger.info("Password change request received");
        String message = authService.changePassword(changePasswordDto);

        logger.info("Password changed successfully");
        return ResponseEntity.ok(message);
    }

    @PutMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileDto updateProfileDto) {

        logger.info("Profile update request received");
        UserProfileResponse userProfileResponse = authService.updateProfile(updateProfileDto);

        logger.info("Profile updated successfully");
        return ResponseEntity.ok(userProfileResponse);
    }
}