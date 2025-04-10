package com.jwt.authentication.service;


import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;

public interface AuthService {
    JWTAuthResponse login(LoginDto loginDto);
}