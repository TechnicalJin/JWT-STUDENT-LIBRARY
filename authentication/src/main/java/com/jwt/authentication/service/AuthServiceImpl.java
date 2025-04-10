package com.jwt.authentication.service;

import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.exception.InvalidLoginException;
import com.jwt.authentication.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public JWTAuthResponse login(LoginDto loginDto) {
        logger.info("Attempting To Authenticate User : {}", loginDto.getUsernameOrEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            logger.info("User Authenticated Successfully : {}", loginDto.getUsernameOrEmail());
            return new JWTAuthResponse(token);
        } catch (BadCredentialsException ex) {
            logger.warn("Invalid login attempt for {}", loginDto.getUsernameOrEmail());
            throw new InvalidLoginException("Invalid username or password");
        } catch (DisabledException ex) {
            logger.warn("Disabled account login attempt for {}", loginDto.getUsernameOrEmail());
            throw new InvalidLoginException("Account is disabled");
        } catch (LockedException ex) {
            logger.warn("Locked account login attempt for {}", loginDto.getUsernameOrEmail());
            throw new InvalidLoginException("Account is locked");
        }
    }
}