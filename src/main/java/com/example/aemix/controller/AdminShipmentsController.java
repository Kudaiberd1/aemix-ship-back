package com.example.aemix.controller;

import com.example.aemix.dto.requests.UpdateShipmentRequest;
import com.example.aemix.service.AdminShipmentsService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/admin")
public class AdminShipmentsController {

    private final AdminShipmentsService adminShipmentsService;

    @PostMapping(value = "/import/shipments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadShipments(@RequestParam("file") MultipartFile file) {
        adminShipmentsService.uploadShipments(file);
        return ResponseEntity.ok(Map.of("message", "Successfully uploaded shipments"));
    }

    @PatchMapping("/shipments/{trackCode}")
    public ResponseEntity<String> updateShipments(@PathVariable String trackCode, @RequestBody UpdateShipmentRequest request) {
        adminShipmentsService.updateShipment(trackCode, request);
        return ResponseEntity.ok("Successfully updated shipments");
    }

    @DeleteMapping("/shipments/{trackCode}")
    public ResponseEntity<String> deleteShipments(@PathVariable String trackCode) {
        adminShipmentsService.deleteShipment(trackCode);
        return ResponseEntity.ok("Successfully deleted shipments");
    }
}
