package com.student.management.service;

import com.student.management.dto.JWTAuthResponse;
import com.student.management.dto.LoginDto;
import com.student.management.dto.StudentDto;
import com.student.management.exception.EmailAlreadyExistsException;
import com.student.management.exception.InvalidLoginException;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Role;
import com.student.management.model.Student;
import com.student.management.repository.RoleRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          StudentRepository studentRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JWTAuthResponse login(LoginDto loginDto) {
        logger.info("Login attempt for email: {}", loginDto.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);

            Student student = studentRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new InvalidLoginException("Student not found"));

            logger.info("Login successful for email: {}", loginDto.getEmail());
            return new JWTAuthResponse(token, student.getEmail(), student.getStudentId(), 
                    student.getFirstname() + " " + student.getLastname());

        } catch (BadCredentialsException e) {
            logger.warn("Invalid login attempt for email: {}", loginDto.getEmail());
            throw new InvalidLoginException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public JWTAuthResponse register(StudentDto studentDto) {
        logger.info("Registration attempt for email: {}", studentDto.getEmail());

        // Check if student already exists
        if (studentRepository.existsByEmail(studentDto.getEmail())) {
            throw new EmailAlreadyExistsException("Student already exists with email: " + studentDto.getEmail());
        }

        if (studentRepository.existsByStudentId(studentDto.getStudentId())) {
            throw new EmailAlreadyExistsException("Student already exists with student ID: " + studentDto.getStudentId());
        }

        // Create new student
        Student student = new Student();
        student.setFirstname(studentDto.getFirstname());
        student.setLastname(studentDto.getLastname());
        student.setEmail(studentDto.getEmail());
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));
        student.setDepartment(studentDto.getDepartment());
        student.setGender(studentDto.getGender());
        student.setDateOfBirth(studentDto.getDateOfBirth());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setAddress(studentDto.getAddress());
        student.setCity(studentDto.getCity());
        student.setState(studentDto.getState());
        student.setCountry(studentDto.getCountry());
        student.setStudentId(studentDto.getStudentId());
        student.setEnrollmentDate(studentDto.getEnrollmentDate() != null ? studentDto.getEnrollmentDate() : LocalDate.now());
        student.setEnrollmentStatus(studentDto.getEnrollmentStatus() != null ? studentDto.getEnrollmentStatus() : EnrollmentStatus.ACTIVE);

        // Assign STUDENT role
        Set<Role> roles = new HashSet<>();
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("STUDENT");
                    return roleRepository.save(newRole);
                });
        roles.add(studentRole);
        student.setRoles(roles);

        Student savedStudent = studentRepository.save(student);

        // Create authorities for token generation
        Set<GrantedAuthority> authorities = savedStudent.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // Generate token with proper authorities
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedStudent.getEmail(), null, authorities);
        String token = jwtTokenProvider.generateToken(authentication);

        logger.info("Registration successful for email: {}", studentDto.getEmail());
        return new JWTAuthResponse(token, savedStudent.getEmail(), savedStudent.getStudentId(),
                savedStudent.getFirstname() + " " + savedStudent.getLastname());
    }
}