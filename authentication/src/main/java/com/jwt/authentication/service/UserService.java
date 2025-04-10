package com.jwt.authentication.service;

import com.jwt.authentication.model.Role;
import com.jwt.authentication.model.User;
import com.jwt.authentication.repository.RoleRepository;
import com.jwt.authentication.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    @Value("${app.roles.admin}")
    private String adminRole;

    @Value("${app.roles.user}")
    private String userRole;

    @Value("${app.roles.student}")
    private String studentRole;

    @Value("${app.roles.librarian}")
    private String librarianRole;

    @EventListener(ApplicationReadyEvent.class)
    public void initRolesAndUsers() {
        logger.info("Initializing roles and users...");
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                initializeData();
                return null;
            });
        } catch (Exception e) {
            logger.error("Initialization failed", e);
        }
    }

    private void initializeData() {
        Role adminRoleEntity = getOrCreateRole(adminRole);
        Role userRoleEntity = getOrCreateRole(userRole);
        Role studentEntity = getOrCreateRole(studentRole);
        Role librarianEntity = getOrCreateRole(librarianRole);

        createUserIfNotExists("admin", "Admin User", "admin@example.com",
                "admin", Set.of(adminRoleEntity));
        createUserIfNotExists("user", "Regular User", "user@example.com",
                "user", Set.of(userRoleEntity));
        createUserIfNotExists("student","Student User", "student@example.com",
                "student", Set.of(studentEntity));
        createUserIfNotExists("librarian","Librarian User", "librarian@example.com",
                "librarian", Set.of(librarianEntity));
    }

    private Role getOrCreateRole(String roleName) {
        Role.RoleType roleType = Role.RoleType.valueOf(roleName);
        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    logger.debug("Creating {} role", roleName);
                    Role newRole = new Role(null, roleType);
                    roleRepository.save(newRole);
                    entityManager.flush();
                    entityManager.refresh(newRole);
                    return newRole;
                });
    }

    private void createUserIfNotExists(String username, String name, String email,
                                       String password, Set<Role> roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            userRepository.save(user);
            logger.info("Created {} user", username);
        }
    }
}