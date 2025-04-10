package com.jwt.authentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.authentication.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.error("Unauthorized Error : {}", authException.getMessage());

        String errorCode = "UNAUTHORIZED";
        String message = authException.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if (authException instanceof BadCredentialsException) {
            message = "Invalid username or password";
        } else if (authException instanceof DisabledException) {
            message = "User account is disabled";
            status = HttpStatus.FORBIDDEN;
            errorCode = "ACCOUNT_DISABLED";
        } else if (authException instanceof LockedException) {
            message = "User account is locked";
            status = HttpStatus.FORBIDDEN;
            errorCode = "ACCOUNT_LOCKED";
        }

        response.setContentType("application/json");
        response.setStatus(status.value());

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                errorCode,
                message,
                request.getRequestURI()
        );

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}