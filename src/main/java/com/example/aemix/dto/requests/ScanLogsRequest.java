package com.example.aemix.dto.requests;

import com.example.aemix.entities.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScanLogsRequest {
    
    @NotBlank(message = "Трек-код обязателен")
    @Size(min = 1, max = 100, message = "Трек-код должен быть от 1 до 100 символов")
    private String trackCode;
    
    private Status oldStatus;
    
    @NotNull(message = "Новый статус обязателен")
    private Status newStatus;
}
