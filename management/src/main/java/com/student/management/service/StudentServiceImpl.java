package com.student.management.service;

import com.student.management.dto.StudentDto;
import com.student.management.exception.*;
import com.student.management.mapper.StudentMapper;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Role;
import com.student.management.model.Student;
import com.student.management.repository.RoleRepository;
import com.student.management.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        logger.info("Attempting to create student with email: {}", studentDto.getEmail());

        try {
            if(studentRepository.existsByEmail(studentDto.getEmail())) {
                logger.warn("Email already exists: {}", studentDto.getEmail());
                throw new EmailAlreadyExistsException(studentDto.getEmail());
            }

            if(studentRepository.existsByStudentId(studentDto.getStudentId())) {
                logger.warn("Student ID already exists: {}", studentDto.getStudentId());
                throw new ResourceAlreadyExistsException("Student", "studentId", studentDto.getStudentId());
            }

            if(studentRepository.existsByPhoneNumber(studentDto.getPhoneNumber())) {
                logger.warn("Phone number already exists: {}", studentDto.getPhoneNumber());
                throw new ResourceAlreadyExistsException("Student", "phoneNumber", studentDto.getPhoneNumber());
            }

            Student student = StudentMapper.mapToStudent(studentDto);

            // Encode the password before saving
            if (studentDto.getPassword() != null && !studentDto.getPassword().isEmpty()) {
                student.setPassword(passwordEncoder.encode(studentDto.getPassword()));
                logger.debug("Password encoded for student: {}", studentDto.getEmail());
            }

            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> {
                        logger.error("STUDENT role not found in database");
                        return new ResourceNotFoundException("Role", "name", "STUDENT");
                    });
            student.setRoles(Collections.singleton(studentRole));

            Student savedStudent = studentRepository.save(student);
            logger.info("Student created successfully with ID: {}", savedStudent.getId());

            if (savedStudent.getId() == null) {
                logger.error("Failed to save student - ID is null");
                throw new DatabaseOperationException("Failed to save student - ID is null");
            }

            return StudentMapper.mapToStudentDto(savedStudent);
        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation while creating student", ex);
            throw new DatabaseOperationException("Failed to create student due to data integrity violation");
        } catch (Exception ex) {
            logger.error("Unexpected error while creating student", ex);
            throw new DatabaseOperationException("Failed to create student due to unexpected error");
        }
    }


    @Override
    public StudentDto getStudentById(Long studentId) {
        logger.debug("Fetching student by ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        return StudentMapper.mapToStudentDto(student);
    }

    @Override
    public List<StudentDto> getAllStudents() {
        logger.debug("Fetching all students");

        try {
            List<Student> students = studentRepository.findAll();
            return students.stream()
                    .map(StudentMapper::mapToStudentDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error fetching all students", ex);
            throw new RuntimeException("Error fetching students");
        }
    }


    @Override
    @Transactional
    public StudentDto updateStudent(Long studentId, StudentDto updateStudent) {
        logger.info("Updating student with ID: {}", studentId);

        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

            if (!student.getEmail().equals(updateStudent.getEmail()) &&
                    studentRepository.existsByEmail(updateStudent.getEmail())) {
                throw new EmailAlreadyExistsException(updateStudent.getEmail());
            }

            if (!student.getStudentId().equals(updateStudent.getStudentId()) &&
                    studentRepository.existsByStudentId(updateStudent.getStudentId())) {
                throw new ResourceAlreadyExistsException("Student", "studentId", updateStudent.getStudentId());
            }

            if (!student.getPhoneNumber().equals(updateStudent.getPhoneNumber()) &&
                    studentRepository.existsByPhoneNumber(updateStudent.getPhoneNumber())) {
                throw new ResourceAlreadyExistsException("Student", "phoneNumber", updateStudent.getPhoneNumber());
            }

            student.setFirstname(updateStudent.getFirstname());
            student.setLastname(updateStudent.getLastname());
            student.setEmail(updateStudent.getEmail());
            student.setDepartment(updateStudent.getDepartment());
            student.setGender(updateStudent.getGender());
            student.setDateOfBirth(updateStudent.getDateOfBirth());
            student.setPhoneNumber(updateStudent.getPhoneNumber());
            student.setAddress(updateStudent.getAddress());
            student.setCity(updateStudent.getCity());
            student.setState(updateStudent.getState());
            student.setCountry(updateStudent.getCountry());
            student.setStudentId(updateStudent.getStudentId());
            student.setEnrollmentDate(updateStudent.getEnrollmentDate());
            student.setEnrollmentStatus(updateStudent.getEnrollmentStatus());

            Student updatedStudent = studentRepository.save(student);
            logger.info("Student updated successfully with ID: {}", studentId);

            return StudentMapper.mapToStudentDto(updatedStudent);
        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation while updating student", ex);
            throw new DataIntegrityViolationException("Failed to update student due to data integrity violation");
        }
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        logger.info("Deleting student with ID: {}", studentId);

        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

            studentRepository.delete(student);
            logger.info("Student deleted successfully with ID: {}", studentId);
        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation while deleting student", ex);
            throw new DataIntegrityViolationException("Failed to delete student due to data integrity violation");
        }
    }

    @Override
    public StudentDto getStudentByStudentId(String studentId) {
        logger.debug("Fetching student by student ID: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentId", studentId));

        return StudentMapper.mapToStudentDto(student);
    }

    @Override
    public List<StudentDto> getStudentsByStatus(EnrollmentStatus status) {
        logger.debug("Fetching students by status: {}", status);

        return studentRepository.findByEnrollmentStatus(status).stream()
                .map(StudentMapper::mapToStudentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDto> getStudentsByEnrollmentDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching students enrolled between {} and {}", startDate, endDate);
        return studentRepository.findByEnrollmentDateBetween(startDate, endDate).stream()
                .map(StudentMapper::mapToStudentDto)
                .collect(Collectors.toList());
    }

    @Override
    public Student getStudentEntityById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }

    @Override
    public Student getStudentEntityByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentId", studentId));
    }

    @Override
    public List<Student> getAllStudentsEntities() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getStudentsEntitiesByStatus(EnrollmentStatus status) {
        return studentRepository.findByEnrollmentStatus(status);
    }

    @Override
    public List<Student> getStudentsEntitiesByEnrollmentDateRange(LocalDate startDate, LocalDate endDate) {
        return studentRepository.findByEnrollmentDateBetween(startDate, endDate);
    }

    @Override
    public boolean isStudentExists(Long studentId) {
        return studentRepository.existsById(studentId);
    }

    @Override
    public StudentDto getCurrentStudent(String email) {
        logger.info("Fetching current student with email: {}", email);
        try {
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
            return StudentMapper.mapToStudentDto(student);
        } catch (Exception e) {
            logger.error("Error fetching current student with email: {}", email, e);
            throw new DatabaseOperationException("Failed to fetch current student", e);
        }
    }

    @Override
    @Transactional
    public void deactivateStudent(Long studentId) {
        logger.info("Deactivating student with ID: {}", studentId);
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
            
            student.setEnrollmentStatus(EnrollmentStatus.INACTIVE);
            studentRepository.save(student);
            logger.info("Successfully deactivated student with ID: {}", studentId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deactivating student with ID: {}", studentId, e);
            throw new DatabaseOperationException("Failed to deactivate student", e);
        }
    }

    @Override
    public List<StudentDto> searchStudentsByName(String name) {
        logger.info("Searching students by name: {}", name);
        try {
            List<Student> students = studentRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(name, name);
            return students.stream()
                    .map(StudentMapper::mapToStudentDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching students by name: {}", name, e);
            throw new DatabaseOperationException("Failed to search students by name", e);
        }
    }

    @Override
    public StudentDto findStudentByEmail(String email) {
        logger.info("Finding student by email: {}", email);
        try {
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
            return StudentMapper.mapToStudentDto(student);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error finding student by email: {}", email, e);
            throw new DatabaseOperationException("Failed to find student by email", e);
        }
    }

    @Override
    public List<StudentDto> getStudentsByDepartment(String department) {
        logger.info("Fetching students by department: {}", department);
        try {
            List<Student> students = studentRepository.findByDepartmentIgnoreCase(department);
            return students.stream()
                    .map(StudentMapper::mapToStudentDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching students by department: {}", department, e);
            throw new DatabaseOperationException("Failed to fetch students by department", e);
        }
    }
}