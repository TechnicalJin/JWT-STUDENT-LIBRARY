package com.jwt.authentication.service;


import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.dto.RegisterDto;

public interface AuthService {
    JWTAuthResponse login(LoginDto loginDto);
    JWTAuthResponse register(RegisterDto registerDto);
    JWTAuthResponse refreshToken(String refreshToken);
}