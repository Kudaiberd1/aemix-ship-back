package com.example.aemix.dto.responses;

import com.example.aemix.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanLogsResponse {
    private Long id;
    private String trackCode;
    private String cityName;
    private String operator;
    private Status oldStatus;
    private Status newStatus;
    private LocalDateTime scannedAt;
}

