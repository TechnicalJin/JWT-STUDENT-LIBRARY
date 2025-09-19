package com.management.library.config;

import com.management.library.security.JwtAuthenticationEntryPoint;
import com.management.library.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyAuthority("ADMIN", "LIBRARIAN", "STUDENT", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasAnyAuthority("STUDENT", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/pending").hasAnyAuthority("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasAnyAuthority("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/loans/active/me").hasAnyAuthority("STUDENT", "USER")
                        .requestMatchers("/api/loans/**").hasAnyAuthority("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").hasAnyAuthority("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/notifications/**").hasAnyAuthority("LIBRARIAN", "ADMIN")
                        .requestMatchers("/api/students/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}