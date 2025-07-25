package com.student.management.controller;

import com.student.management.dto.StudentDto;
import com.student.management.dto.StudentResponse;
import com.student.management.mapper.StudentMapper;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import com.student.management.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private final StudentService studentService;

    @Autowired
    private final StudentRepository studentRepository;

    @GetMapping("/validate/{studentId}")
    public ResponseEntity<Boolean> validateStudent(@PathVariable String studentId) {
        boolean exists = studentService.isStudentExists(Long.valueOf(studentId));
        return ResponseEntity.ok(exists);
    }



    @GetMapping("/exists/{studentId}")
    public boolean doesStudentExist(@PathVariable Long studentId) {
        return studentRepository.existsById(studentId);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        logger.info("Creating new student: {}", studentDto.getEmail());
        StudentDto savedStudent = studentService.createStudent(studentDto);
        logger.info("Student created successfully with ID: {}", savedStudent.getId());
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    @GetMapping("{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable("id") Long studentId) {
        logger.debug("Fetching student with ID: {}", studentId);
        Student student = studentService.getStudentEntityById(studentId);
        StudentResponse studentResponse = StudentMapper.mapToStudentResponse(student);
        logger.debug("Student found: {}", studentResponse.getEmail());
        return ResponseEntity.ok(studentResponse);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
*/
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        logger.debug("Fetching all students");
        List<Student> students = studentService.getAllStudentsEntities();
        List<StudentResponse> studentResponses = students.stream()
                .map(StudentMapper::mapToStudentResponse)
                .collect(Collectors.toList());
        logger.debug("Found {} students", studentResponses.size());
        return ResponseEntity.ok(studentResponses);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable("id") Long studentId,
            @RequestBody @Valid StudentDto updateStudent) {
        logger.info("Updating student with ID: {}", studentId);
        StudentDto studentDto = studentService.updateStudent(studentId, updateStudent);
        logger.info("Student updated successfully: {}", studentDto.getEmail());
        return ResponseEntity.ok(studentDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") Long studentId) {
        logger.info("Deleting student with ID: {}", studentId);
        studentService.deleteStudent(studentId);
        logger.info("Student deleted successfully with ID: {}", studentId);
        return ResponseEntity.ok("Student deleted successfully");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<StudentResponse> getStudentByStudentId(@PathVariable String studentId) {
        Student student = studentService.getStudentEntityByStudentId(studentId);
        StudentResponse studentResponse = StudentMapper.mapToStudentResponse(student);
        return ResponseEntity.ok(studentResponse);
    }

   /* @GetMapping("/studentId/{studentId}")
    public ResponseEntity<StudentResponse> getStudentByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getStudentByStudentId(studentId));
    }*/

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentResponse>> getStudentsByStatus(@PathVariable EnrollmentStatus status) {
        List<Student> students = studentService.getStudentsEntitiesByStatus(status);
        List<StudentResponse> studentResponses = students.stream()
                .map(StudentMapper::mapToStudentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentResponses);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/enrollment-range")
    public ResponseEntity<List<StudentResponse>> getStudentsByEnrollmentDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<Student> students = studentService.getStudentsEntitiesByEnrollmentDateRange(startDate, endDate);
        List<StudentResponse> studentResponses = students.stream()
                .map(StudentMapper::mapToStudentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentResponses);
    }
}