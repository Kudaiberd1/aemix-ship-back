package com.example.aemix.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record TelegramStartAppRequest(
        @NotBlank(message = "Token is required")
        String token
) {}
