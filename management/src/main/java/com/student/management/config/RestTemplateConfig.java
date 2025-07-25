/*
package com.student.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    private final JwtAuthorizationInterceptor jwtAuthorizationInterceptor;

    public RestTemplateConfig(JwtAuthorizationInterceptor jwtAuthorizationInterceptor) {
        this.jwtAuthorizationInterceptor = jwtAuthorizationInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }

        interceptors.add(jwtAuthorizationInterceptor);
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}*/
