package com.management.library.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.library.dto.ErrorResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String token = getTokenFromRequest(request);
            logger.debug("Token from request: {}", token);

            if (StringUtils.hasText(token)) {
                logger.debug("Validating token...");
                if (!jwtTokenProvider.validateToken(token)) {
                    handleInvalidToken(response, request);
                    return;
                }

                String username = jwtTokenProvider.getUsername(token);
                if (username == null) {
                    logger.error("Username extracted from token is null");
                    handleInvalidToken(response, request);
                    return;
                }

                logger.debug("Extracted username: {}", username);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Claims claims = jwtTokenProvider.extractAllClaims(token);
                    if (claims == null) {
                        logger.error("Claims could not be extracted from token");
                        handleInvalidToken(response, request);
                        return;
                    }
                    Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
                    logger.debug("Authorities extracted from JWT: {}", authorities);

                    // Create a simple user principal instead of using UserDetailsService
                    // which returns empty authorities
                    UserDetails userPrincipal = new org.springframework.security.core.userdetails.User(
                            username, 
                            "", 
                            authorities
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    authorities
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authenticated user: {} with authorities: {}", username, authorities);
                }
            } else {
                logger.debug("No valid token found in request");
            }
        } catch (AuthenticationException ex) {
            handleAuthenticationException(response, request, ex);
            return;
        } catch (Exception ex) {
            handleGenericException(response, request, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("Invalid JWT token");
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                "Invalid JWT token",
                request.getRequestURI()
        );

        sendErrorResponse(response, errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private void handleAuthenticationException(HttpServletResponse response,
                                               HttpServletRequest request,
                                               AuthenticationException ex) throws IOException {
        logger.error("Authentication error: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );

        sendErrorResponse(response, errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private void handleGenericException(HttpServletResponse response,
                                        HttpServletRequest request,
                                        Exception ex) throws IOException {
        logger.error("Authentication processing error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred during authentication",
                request.getRequestURI()
        );

        sendErrorResponse(response, errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   ErrorResponse errorResponse,
                                   HttpStatus status) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(jsonResponse);
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        if (claims.containsKey("roles")) {
            String rolesString = claims.get("roles", String.class);
            logger.debug("Roles from JWT: {}", rolesString);
            if (rolesString != null && !rolesString.isEmpty()) {
                return Arrays.stream(rolesString.split(","))
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new) // Don't add ROLE_ prefix, use as-is
                        .collect(Collectors.toList());
            }
        }
        logger.warn("No roles found in JWT claims");
        return Collections.emptyList();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}