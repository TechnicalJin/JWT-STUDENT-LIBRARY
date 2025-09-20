package com.student.management.controller;

import com.student.management.dto.JWTAuthResponse;
import com.student.management.dto.LoginDto;
import com.student.management.dto.StudentDto;
import com.student.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        logger.info("Login request received for email: {}", loginDto.getEmail());
        JWTAuthResponse jwtAuthResponse = authService.login(loginDto);
        logger.info("Login successful for email: {}", loginDto.getEmail());
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<JWTAuthResponse> register(@Valid @RequestBody StudentDto studentDto) {
        logger.info("Registration request received for email: {}", studentDto.getEmail());
        JWTAuthResponse jwtAuthResponse = authService.register(studentDto);
        logger.info("Registration successful for email: {}", studentDto.getEmail());
        return ResponseEntity.ok(jwtAuthResponse);
    }
}