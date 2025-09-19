package com.management.library.service;

import com.management.library.dto.StudentDTO;
import com.management.library.exception.StudentServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentServiceClient {

    private final RestTemplate restTemplate;
    private final InterServiceClient interServiceClient;

    @Value("${student.service.url}")
    private String studentServiceUrl;


    public boolean doesStudentExist(Long studentId) {
        String url = "http://localhost:8081/api/students/exists/" + studentId; // URL of Student Service
        return Boolean.TRUE.equals(interServiceClient.getForObject(url, Boolean.class));
    }

    public StudentDTO getStudentById(Long studentId) {
        String url = studentServiceUrl + "/api/students/" + studentId;
        log.debug("Fetching student from external service: {}", url);
        try {
            ResponseEntity<StudentDTO> response = interServiceClient.exchange(url, org.springframework.http.HttpMethod.GET, null, StudentDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to fetch student. Status: {}", response.getStatusCode());
                throw new StudentServiceException("Failed to fetch student. Status: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error communicating with student service", e);
            throw new StudentServiceException("Error communicating with student service");
        }
    }
}