package com.student.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTAuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String studentId;
    private String name;

    public JWTAuthResponse(String accessToken, String email, String studentId, String name) {
        this.accessToken = accessToken;
        this.email = email;
        this.studentId = studentId;
        this.name = name;
    }
}