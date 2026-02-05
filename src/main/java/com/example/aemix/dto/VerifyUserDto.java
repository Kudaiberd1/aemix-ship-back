package com.example.aemix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDto {
    @NotBlank(message = "Email or Telegram ID is required")
    private String emailOrTelegramId;

    @NotBlank(message = "Verification code is required")
    private String verificationCode;
}
