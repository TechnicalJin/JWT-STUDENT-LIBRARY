package com.student.management.config;

import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Role;
import com.student.management.model.Student;
import com.student.management.repository.RoleRepository;
import com.student.management.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create roles
        Role studentRole = null;
        Role adminRole = null;
        Role userRole = null;

        if (roleRepository.findByName("STUDENT").isEmpty()){
            studentRole = new Role();
            studentRole.setName("STUDENT");
            studentRole = roleRepository.save(studentRole);
        } else {
            studentRole = roleRepository.findByName("STUDENT").get();
        }

        if (roleRepository.findByName("ADMIN").isEmpty()){
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole = roleRepository.save(adminRole);
        } else {
            adminRole = roleRepository.findByName("ADMIN").get();
        }

        if (roleRepository.findByName("USER").isEmpty()){
            userRole = new Role();
            userRole.setName("USER");
            userRole = roleRepository.save(userRole);
        } else {
            userRole = roleRepository.findByName("USER").get();
        }

        // Create default admin user if not exists (using unique email and phone)
        if (!studentRepository.existsByEmail("admin@management.com")) {
            Student admin = new Student();
            admin.setFirstname("Admin");
            admin.setLastname("Management");
            admin.setEmail("admin@management.com");
            admin.setDepartment("ADMINISTRATION");
            admin.setGender("MALE");
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setPhoneNumber("+9999999999");
            admin.setAddress("Admin Address");
            admin.setCity("Admin City");
            admin.setState("Admin State");
            admin.setCountry("Admin Country");
            admin.setStudentId("ADMIN999");
            admin.setEnrollmentDate(LocalDate.now());
            admin.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            studentRepository.save(admin);
        }
        
        // Create simple admin user for testing with email "admin"
        if (!studentRepository.existsByEmail("admin")) {
            Student simpleAdmin = new Student();
            simpleAdmin.setFirstname("Admin");
            simpleAdmin.setLastname("User");
            simpleAdmin.setEmail("admin");
            simpleAdmin.setDepartment("ADMINISTRATION");
            simpleAdmin.setGender("MALE");
            simpleAdmin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            simpleAdmin.setPhoneNumber("+1234567890");
            simpleAdmin.setAddress("Admin Address");
            simpleAdmin.setCity("Admin City");
            simpleAdmin.setState("Admin State");
            simpleAdmin.setCountry("Admin Country");
            simpleAdmin.setStudentId("ADMIN001");
            simpleAdmin.setEnrollmentDate(LocalDate.now());
            simpleAdmin.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
            simpleAdmin.setPassword(passwordEncoder.encode("admin"));
            simpleAdmin.setRoles(Set.of(adminRole));
            studentRepository.save(simpleAdmin);
        }
    }
}