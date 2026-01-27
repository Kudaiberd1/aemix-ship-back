package com.example.aemix.controller;

import com.example.aemix.dto.responses.UserResponse;
import com.example.aemix.entity.enums.Stage;
import com.example.aemix.service.AdminShipmentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminShipmentsController {

    private final AdminShipmentsService adminShipmentsService;

    @PostMapping(value = "/import/shipments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadShipments(@RequestParam("file") MultipartFile file) {
        adminShipmentsService.uploadShipments(file);
        return ResponseEntity.ok(Map.of("message", "Successfully uploaded shipments"));
    }

    @PostMapping("/shipments")
    public ResponseEntity<Map<String, String>> createShipments(@RequestBody String trackCode) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getAuthorities());
        adminShipmentsService.createShipments(trackCode);
        return ResponseEntity.ok(Map.of("message", "Successfully created shipments"));
    }

    @PutMapping("/shipments/russia/{trackCode}")
    public ResponseEntity<String> updateShipments(@PathVariable String trackCode) {
        adminShipmentsService.updateShipment(trackCode, Stage.RUSSIA_CUSTOMS);
        return ResponseEntity.ok("Successfully updated shipments");
    }

    @PutMapping("/shipments/pickup/{trackCode}")
    public ResponseEntity<String> updateShipment(@PathVariable String trackCode) {
        adminShipmentsService.updateShipment(trackCode, Stage.PICKUP_POINT);
        return ResponseEntity.ok("Successfully updated shipments");
    }

    @DeleteMapping("/shipments/{trackCode}")
    public ResponseEntity<String> deleteShipments(@PathVariable String trackCode) {
        adminShipmentsService.deleteShipment(trackCode);
        return ResponseEntity.ok("Successfully deleted shipments");
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable String userId, @RequestBody String role) {
        adminShipmentsService.changeUserRole(userId, role);
        return ResponseEntity.ok("Successfully changed user role");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = adminShipmentsService.getUsers();
        return ResponseEntity.ok(users);
    }
}
