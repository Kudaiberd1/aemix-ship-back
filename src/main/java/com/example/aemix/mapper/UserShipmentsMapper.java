package com.example.aemix.mapper;

import com.example.aemix.dto.responses.UserShipmentsResponse;
import com.example.aemix.entity.Shipments;
import com.example.aemix.entity.UserShipments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = Shipments.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserShipmentsMapper {

    @Mapping(target = "shipmentTrackCode", source = "shipment.trackCode")
    @Mapping(target = "stage", source = "shipment.curentStage")
    UserShipmentsResponse toDto(UserShipments userShipments);
}