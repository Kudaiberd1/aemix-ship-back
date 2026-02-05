package com.example.aemix.mappers;

import com.example.aemix.dto.responses.ScanLogsResponse;
import com.example.aemix.entities.ScanLogs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScanLogsMapper {

    @Mapping(target = "trackCode", expression = "java(getTrackCode(entity))")
    @Mapping(target = "cityName", expression = "java(getCityName(entity))")
    @Mapping(target = "operator", expression = "java(getOperator(entity))")
    ScanLogsResponse toDto(ScanLogs entity);

    default String getTrackCode(ScanLogs e) {
        return e.getOrder() != null ? e.getOrder().getTrackCode() : null;
    }

    default String getCityName(ScanLogs e) {
        return e.getOrder() != null && e.getOrder().getCity() != null
                ? e.getOrder().getCity().getName()
                : null;
    }

    default String getOperator(ScanLogs e) {
        return e.getUser() != null ? e.getUser().getEmailOrTelegramId() : null;
    }
}