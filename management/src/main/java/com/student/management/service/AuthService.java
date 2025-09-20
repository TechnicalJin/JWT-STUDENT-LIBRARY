package com.student.management.service;

import com.student.management.dto.JWTAuthResponse;
import com.student.management.dto.LoginDto;
import com.student.management.dto.StudentDto;

public interface AuthService {
    
    JWTAuthResponse login(LoginDto loginDto);
    JWTAuthResponse register(StudentDto studentDto);
}