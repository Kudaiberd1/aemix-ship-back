package com.example.aemix.dto.responses;

import com.example.aemix.entity.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String password;
    private Role role;
    private Boolean isVerified;
}
