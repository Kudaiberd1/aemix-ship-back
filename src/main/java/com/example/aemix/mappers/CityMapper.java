package com.example.aemix.mappers;

import com.example.aemix.dto.responses.CityResponse;
import com.example.aemix.entities.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {
    CityResponse toDto(City city);
}
