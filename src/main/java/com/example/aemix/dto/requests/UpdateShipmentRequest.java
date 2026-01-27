package com.example.aemix.dto.requests;

import com.example.aemix.entity.enums.Stage;
import lombok.Data;

@Data
public class UpdateShipmentRequest {
    Stage stage;
}
