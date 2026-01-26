package com.example.aemix.repository;

import com.example.aemix.entity.UserShipments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserShipmentsRepository extends JpaRepository<UserShipments, Long> {
    Optional<List<UserShipments>> findAllByUserId(Integer userId);

    void deleteByUserIdAndShipmentTrackCode(Integer userId, String shipmentTrackCode);
}
