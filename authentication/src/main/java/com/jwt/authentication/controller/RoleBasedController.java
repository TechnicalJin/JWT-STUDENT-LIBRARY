package com.jwt.authentication.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoleBasedController {

    private static final Logger logger = LoggerFactory.getLogger(RoleBasedController.class);

    @Value("${app.roles.admin}")
    private String adminRole;

    @Value("${app.roles.user}")
    private String userRole;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminDashboard() {
        logger.info("Admin dashboard accessed");
        return "Welcome to Admin Dashboard";
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public String userProfile() {
        logger.info("User Profile accessed");
        return "Welcome to User Profile";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        logger.info("Public Endpoint accessed");
        return "This is a public endpoint";
    }
}