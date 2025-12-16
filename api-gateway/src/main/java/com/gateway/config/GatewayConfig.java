package main.java.com.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration
 * Defines routing rules for all microservices in the system
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Authentication Service Routes
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://authentication-service"))
                
                // Student Management Service Routes
                .route("student-service", r -> r
                        .path("/api/students/**")
                        .uri("lb://management-service"))
                
                // Library Service Routes - Books
                .route("library-books", r -> r
                        .path("/api/books/**")
                        .uri("lb://library-service"))
                
                // Library Service Routes - Loans
                .route("library-loans", r -> r
                        .path("/api/loans/**")
                        .uri("lb://library-service"))
                
                // Library Service Routes - Reservations
                .route("library-reservations", r -> r
                        .path("/api/reservations/**")
                        .uri("lb://library-service"))
                
                // Library Service Routes - Library-specific student endpoints
                .route("library-students", r -> r
                        .path("/api/library/students/**")
                        .uri("lb://library-service"))
                
                .build();
    }
}