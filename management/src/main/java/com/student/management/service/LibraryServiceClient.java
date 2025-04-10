/*
package com.student.management.service;

import com.student.management.dto.LibraryStudentDto;
import com.student.management.exception.LibraryServiceException;
import com.student.management.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LibraryServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${library.service.base-url}")
    private String libraryServiceBaseUrl;

    public LibraryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(value = {LibraryServiceException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void registerStudentInLibrary(Student student) {
        try {
            String url = libraryServiceBaseUrl + "/api/library/students";

            LibraryStudentDto libraryStudentDto = convertToLibraryStudentDto(student);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LibraryStudentDto> requestEntity = new HttpEntity<>(libraryStudentDto, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to register student in library. Status code: {}", response.getStatusCodeValue());
                throw new LibraryServiceException("Failed to register student in library service");
            }

            logger.info("Student {} successfully registered in library service", student.getEmail());
        } catch (Exception e) {
            logger.error("Error while registering student in library service", e);
            throw new LibraryServiceException("Error communicating with library service", e);
        }
    }

    private LibraryStudentDto convertToLibraryStudentDto(Student student) {
        LibraryStudentDto dto = new LibraryStudentDto();
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstname());
        dto.setLastName(student.getLastname());
        dto.setEmail(student.getEmail());
        dto.setDepartment(student.getDepartment());
        return dto;
    }
}*/
