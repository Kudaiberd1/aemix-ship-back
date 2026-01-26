package com.example.aemix.service;

import com.example.aemix.dto.requests.AddShipmentRequest;
import com.example.aemix.dto.responses.ShipmentResponse;
import com.example.aemix.dto.responses.UserShipmentsResponse;
import com.example.aemix.entity.Shipments;
import com.example.aemix.entity.UserShipments;
import com.example.aemix.mapper.ShipmentsMapper;
import com.example.aemix.mapper.UserShipmentsMapper;
import com.example.aemix.repository.ShipmentsRepository;
import com.example.aemix.repository.UserShipmentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserShipmentsService {

    private final UserShipmentsRepository userShipmentsRepository;
    private final ShipmentsRepository shipmentsRepository;
    private final ShipmentsMapper shipmentsMapper;
    private final UserShipmentsMapper userShipmentsMapper;

    public ShipmentResponse addShipment(AddShipmentRequest addShipmentRequest, Jwt jwt) {
        Integer userId = ((Number) jwt.getClaim("id")).intValue();
        Shipments shipment = shipmentsRepository.findByTrackCode(addShipmentRequest.getTrackCode()).orElseThrow(() -> new IllegalArgumentException("Invalid Track Code"));

        UserShipments userShipments = UserShipments.builder()
                .userId(userId)
                .shipment(shipment)
                .aliasName(addShipmentRequest.getAliasName())
                .build();

        UserShipments newShipment = userShipmentsRepository.save(userShipments);

        if (shipment.getUserShipments() != null) {
            shipment.getUserShipments().add(newShipment);
        } else {
            shipment.setUserShipments(new java.util.ArrayList<>(java.util.List.of(newShipment)));
        }

        return shipmentsMapper.toDto(shipment);
    }


    public List<UserShipmentsResponse> getAllShipments(Jwt jwt) {
        Integer userId = ((Number) jwt.getClaim("id")).intValue();
        List<UserShipments> shipments = userShipmentsRepository.findAllByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Invalid User Id"));
        return shipments.stream().map(userShipmentsMapper::toDto).toList();
    }

    @Transactional
    public void deleteShipment(String trackCode, Jwt jwt) {
        Integer userId = ((Number) jwt.getClaim("id")).intValue();
        userShipmentsRepository.deleteByUserIdAndShipmentTrackCode(userId, trackCode);
    }
}
