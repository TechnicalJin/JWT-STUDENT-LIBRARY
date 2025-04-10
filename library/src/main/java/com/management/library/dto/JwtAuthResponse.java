package com.management.library.dto;

public record JwtAuthResponse(String accessToken) {
    public String getTokenType() {
        return "Bearer";
    }
}
