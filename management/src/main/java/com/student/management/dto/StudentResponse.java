package com.student.management.dto;

import com.student.management.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String department;
    private String gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String studentId;
    private LocalDate enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private Set<String> roles;
}