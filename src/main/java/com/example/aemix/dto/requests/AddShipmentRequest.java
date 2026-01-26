package com.example.aemix.dto.requests;

import lombok.Data;

@Data
public class AddShipmentRequest {
    private String trackCode;
    private String aliasName;
}
