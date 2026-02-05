package com.example.aemix.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddUserOrderRequest {
    @NotBlank(message = "Трек-код не может быть пустым")
    @Size(min = 1, max = 100, message = "Трек-код должен быть от 1 до 100 символов")
    private String trackCode;
    
    @Size(max = 255, message = "Название заказа не должно превышать 255 символов")
    private String title;
}
