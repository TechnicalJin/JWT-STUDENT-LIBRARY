package com.student.management.mapper;

import com.student.management.dto.StudentDto;
import com.student.management.dto.StudentResponse;
import com.student.management.model.Role;
import com.student.management.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class StudentMapper {

    private static final Logger logger = LoggerFactory.getLogger(StudentMapper.class);

    public static StudentDto mapToStudentDto(Student student){
        logger.debug("Mapping Student to StudentDto for ID: {}", student.getId());

        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setFirstname(student.getFirstname());
        studentDto.setLastname(student.getLastname());
        studentDto.setEmail(student.getEmail());
        studentDto.setDepartment(student.getDepartment());
        studentDto.setGender(student.getGender());
        studentDto.setDateOfBirth(student.getDateOfBirth());
        studentDto.setPhoneNumber(student.getPhoneNumber());
        studentDto.setAddress(student.getAddress());
        studentDto.setCity(student.getCity());
        studentDto.setState(student.getState());
        studentDto.setCountry(student.getCountry());
        studentDto.setStudentId(student.getStudentId());
        studentDto.setEnrollmentDate(student.getEnrollmentDate());
        studentDto.setEnrollmentStatus(student.getEnrollmentStatus());
        studentDto.setRoles(student.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        return studentDto;
    }

    public static Student mapToStudent(StudentDto studentDto){
        logger.debug("Mapping StudentDto to Student for email: {}", studentDto.getEmail());

        Student student = new Student();
        student.setId(studentDto.getId());
        student.setFirstname(studentDto.getFirstname());
        student.setLastname(studentDto.getLastname());
        student.setEmail(studentDto.getEmail());
        student.setDepartment(studentDto.getDepartment());
        student.setGender(studentDto.getGender());
        student.setDateOfBirth(studentDto.getDateOfBirth());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setAddress(studentDto.getAddress());
        student.setCity(studentDto.getCity());
        student.setState(studentDto.getState());
        student.setCountry(studentDto.getCountry());
        student.setStudentId(studentDto.getStudentId());
        student.setEnrollmentDate(studentDto.getEnrollmentDate());
        student.setEnrollmentStatus(studentDto.getEnrollmentStatus());

        return student;
    }

    public static StudentResponse mapToStudentResponse(Student student) {
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setId(student.getId());
        studentResponse.setFirstname(student.getFirstname());
        studentResponse.setLastname(student.getLastname());
        studentResponse.setEmail(student.getEmail());
        studentResponse.setDepartment(student.getDepartment());
        studentResponse.setGender(student.getGender());
        studentResponse.setDateOfBirth(student.getDateOfBirth());
        studentResponse.setPhoneNumber(student.getPhoneNumber());
        studentResponse.setAddress(student.getAddress());
        studentResponse.setCity(student.getCity());
        studentResponse.setState(student.getState());
        studentResponse.setCountry(student.getCountry());
        studentResponse.setStudentId(student.getStudentId());
        studentResponse.setEnrollmentDate(student.getEnrollmentDate());
        studentResponse.setEnrollmentStatus(student.getEnrollmentStatus());
        studentResponse.setRoles(student.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));

        return studentResponse;
    }
}