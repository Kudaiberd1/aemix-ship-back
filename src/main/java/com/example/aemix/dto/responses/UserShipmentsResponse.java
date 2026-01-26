package com.example.aemix.dto.responses;

import com.example.aemix.entity.enums.Stage;
import lombok.Data;

@Data
public class UserShipmentsResponse {
    private Integer id;
    private Integer userId;
    private String shipmentTrackCode;
    private Stage stage;
    private String aliasName;
}
