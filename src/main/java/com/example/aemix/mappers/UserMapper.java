package com.example.aemix.mappers;

import com.example.aemix.dto.responses.UserResponse;
import com.example.aemix.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
    User toEntity(UserResponse userResponse);
}
