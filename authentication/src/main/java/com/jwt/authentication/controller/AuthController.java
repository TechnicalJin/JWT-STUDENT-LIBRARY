package com.jwt.authentication.controller;

import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
}