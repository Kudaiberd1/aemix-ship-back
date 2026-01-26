package com.example.aemix.mapper;

import com.example.aemix.dto.responses.ShipmentResponse;
import com.example.aemix.entity.Shipments;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = Shipments.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ShipmentsMapper {
    ShipmentResponse toDto(Shipments shipments);
}
