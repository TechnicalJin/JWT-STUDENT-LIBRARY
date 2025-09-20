package com.jwt.authentication.service;


import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.dto.RegisterDto;
import com.jwt.authentication.dto.ChangePasswordDto;
import com.jwt.authentication.dto.UpdateProfileDto;
import com.jwt.authentication.dto.UserProfileResponse;

public interface AuthService {
    JWTAuthResponse login(LoginDto loginDto);
    JWTAuthResponse register(RegisterDto registerDto);
    JWTAuthResponse refreshToken(String refreshToken);
    String changePassword(ChangePasswordDto changePasswordDto);
    UserProfileResponse updateProfile(UpdateProfileDto updateProfileDto);
}