package com.student.management.repository;

import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByStudentId(String studentId);
    boolean existsById(Long studentId);

    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByEmail(String email);
    List<Student> findByDepartment(String department);
    List<Student> findByDepartmentIgnoreCase(String department);
    List<Student> findByFirstnameContainingIgnoreCase(String firstname);
    List<Student> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname, String lastname);
    List<Student> findByEnrollmentStatus(EnrollmentStatus status);
    List<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);
}