package com.example.aemix.dto.requests;

import com.example.aemix.entities.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @NotNull(message = "Роль обязательна")
    private Role role;
}
