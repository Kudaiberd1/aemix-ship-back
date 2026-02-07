package com.example.aemix.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateInstructionLinkRequest {

    @Size(max = 255)
    private String title;

    @NotBlank(message = "Ссылка обязательна")
    @Size(max = 2048)
    private String link;

    @Size(max = 500)
    private String subtitle;
}
