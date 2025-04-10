package com.student.management.dto;

import com.student.management.model.EnrollmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {

    private Long id;

    @Size(min = 2, message = "First name should have at least 2 characters")
    private String firstname;

    @NotEmpty(message = "Last name should not be empty")
    private String lastname;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Enter Valid Email")
    private String email;

    @NotEmpty(message = "Department should not be empty")
    private String department;

    @NotEmpty(message = "Gender should not be empty")
    private String gender;

    @NotNull(message = "Date of birth should not be empty")
    @Past(message = "Date of birth should be in the past")
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Phone number should not be empty")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",
            message = "Invalid phone number format")
    private String phoneNumber;

    private String address;

    private String city;

    private String state;

    private String country;

    @NotEmpty(message = "Student ID should not be empty")
    private String studentId;

    @NotNull(message = "Enrollment date should not be empty")
    private LocalDate enrollmentDate;

    private EnrollmentStatus enrollmentStatus;

    private Set<String> roles;
}