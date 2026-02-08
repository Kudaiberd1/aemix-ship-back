package com.example.aemix.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record TelegramInitDataRequest(
        @NotBlank(message = "initData is required")
        String initData
) {}
