package com.jwt.authentication.service;

import com.jwt.authentication.dto.JWTAuthResponse;
import com.jwt.authentication.dto.LoginDto;
import com.jwt.authentication.dto.RegisterDto;
import com.jwt.authentication.exception.InvalidLoginException;
import com.jwt.authentication.exception.InvalidTokenException;
import com.jwt.authentication.model.RefreshToken;
import com.jwt.authentication.model.Role;
import com.jwt.authentication.model.User;
import com.jwt.authentication.repository.RoleRepository;
import com.jwt.authentication.repository.UserRepository;
import com.jwt.authentication.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenService refreshTokenService;

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

            // Get user for refresh token generation
            User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail())
                    .orElseThrow(() -> new InvalidLoginException("User not found"));

            // Create refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            logger.info("User Authenticated Successfully : {}", loginDto.getUsernameOrEmail());
            return new JWTAuthResponse(token, refreshToken.getToken());
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

    @Override
    public JWTAuthResponse register(RegisterDto registerDto) {
        logger.info("Attempting to register user: {}", registerDto.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            logger.warn("Registration failed - username already exists: {}", registerDto.getUsername());
            throw new InvalidLoginException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", registerDto.getEmail());
            throw new InvalidLoginException("Email already exists");
        }

        // Get the STUDENT role
        Role studentRole = roleRepository.findByName(Role.RoleType.STUDENT)
                .orElseThrow(() -> {
                    logger.error("STUDENT role not found during registration");
                    return new RuntimeException("Default role not found");
                });

        // Create new user
        User user = User.builder()
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(Set.of(studentRole))
                .build();

        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());

        // Authenticate the newly registered user and generate JWT token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerDto.getUsername(),
                        registerDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        // Create refresh token for newly registered user
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        logger.info("JWT token generated for newly registered user: {}", savedUser.getUsername());
        return new JWTAuthResponse(token, refreshToken.getToken());
    }

    @Override
    public JWTAuthResponse refreshToken(String refreshToken) {
        logger.info("Attempting to refresh token");

        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Invalid refresh token provided");
                    return new InvalidTokenException("Refresh token is not valid");
                });

        // Verify token is not expired
        refreshTokenService.verifyExpiration(refreshTokenEntity);

        User user = refreshTokenEntity.getUser();
        
        // Load user authorities from database
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        
        // Create authentication object with proper authorities
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                authorities
        );

        String newAccessToken = jwtTokenProvider.generateToken(authentication);

        // Create new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        logger.info("Token refreshed successfully for user: {}", user.getUsername());
        return new JWTAuthResponse(newAccessToken, newRefreshToken.getToken());
    }
}