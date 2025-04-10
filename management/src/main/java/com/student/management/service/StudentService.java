package com.student.management.service;

import com.student.management.dto.StudentDto;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Student;

import java.time.LocalDate;
import java.util.List;

public interface StudentService {

    StudentDto createStudent(StudentDto studentDto);
    StudentDto getStudentById(Long studentId);
    StudentDto getStudentByStudentId(String studentId);
    List<StudentDto> getAllStudents();
    List<StudentDto> getStudentsByStatus(EnrollmentStatus status);
    List<StudentDto> getStudentsByEnrollmentDateRange(LocalDate startDate, LocalDate endDate);
    StudentDto updateStudent(Long studentId, StudentDto updateStudent);
    void deleteStudent(Long studentId);

    Student getStudentEntityById(Long studentId);

    Student getStudentEntityByStudentId(String studentId);

    List<Student> getAllStudentsEntities();

    List<Student> getStudentsEntitiesByStatus(EnrollmentStatus status);

    List<Student> getStudentsEntitiesByEnrollmentDateRange(LocalDate startDate, LocalDate endDate);

    boolean isStudentExists(Long studentId);
}