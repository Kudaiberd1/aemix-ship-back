package com.example.aemix.dto.responses;

import com.example.aemix.entity.enums.Stage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShipmentResponse {
    private UUID id;
    private String trackCode;
    private Stage curentStage;
    private LocalDateTime createdAt;
}
