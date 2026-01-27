package com.example.aemix.mapper;

import com.example.aemix.dto.responses.UserResponse;
import com.example.aemix.entity.Shipments;
import com.example.aemix.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = Shipments.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserResponse toDto(User user);
}
