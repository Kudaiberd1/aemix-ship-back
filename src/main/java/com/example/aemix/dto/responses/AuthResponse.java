package com.example.aemix.dto.responses;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
}