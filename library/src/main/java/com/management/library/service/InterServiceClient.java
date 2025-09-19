package com.management.library.service;

import com.management.library.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InterServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(InterServiceClient.class);
    private final RestTemplate restTemplate;

    public InterServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getForObject(String url, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithAuth();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        logger.debug("Making GET request to: {} with headers: {}", url, headers.keySet());
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithAuth();
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        
        logger.debug("Making {} request to: {} with headers: {}", method, url, headers.keySet());
        return restTemplate.exchange(url, method, entity, responseType);
    }

    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        
        // Get the current JWT token from the request context
        String authHeader = JwtTokenUtil.getAuthorizationHeader();
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
            logger.debug("Added Authorization header for inter-service call");
        } else {
            logger.warn("No JWT token found for inter-service call");
        }
        
        headers.set("Content-Type", "application/json");
        return headers;
    }
}