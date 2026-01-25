package com.example.aemix.dto.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}