package com.example.aemix.controller;

import com.example.aemix.dto.requests.AddShipmentRequest;
import com.example.aemix.dto.responses.ShipmentResponse;
import com.example.aemix.dto.responses.UserShipmentsResponse;
import com.example.aemix.service.UserShipmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/user/shipments")
public class UserShipmentsController {

    private final UserShipmentsService userShipmentsService;

    @PostMapping()
    public ResponseEntity<ShipmentResponse> addShipment(@RequestBody AddShipmentRequest addShipmentRequest, @AuthenticationPrincipal Jwt jwt) {
        ShipmentResponse shipmentResponse = userShipmentsService.addShipment(addShipmentRequest, jwt);
        return ResponseEntity.ok(shipmentResponse);
    }

    @GetMapping()
    public ResponseEntity<List<UserShipmentsResponse>> getAllShipments(@AuthenticationPrincipal Jwt jwt) {
        List<UserShipmentsResponse> shipments = userShipmentsService.getAllShipments(jwt);
        return ResponseEntity.ok(shipments);
    }

    @DeleteMapping("/{trackCode}")
    public ResponseEntity<String> deleteShipment(@PathVariable String trackCode, @AuthenticationPrincipal Jwt jwt) {
        userShipmentsService.deleteShipment(trackCode, jwt);
        return ResponseEntity.ok("Successfully deleted");
    }

}
