package com.example.aemix.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Email or Telegram ID is required")
    private String emailOrTelegramId;

    @NotBlank(message = "Password is required")
    private String password;
}