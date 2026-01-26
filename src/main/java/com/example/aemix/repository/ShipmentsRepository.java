package com.example.aemix.repository;

import com.example.aemix.entity.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentsRepository extends JpaRepository<Shipments, UUID> {
    Optional<Shipments> findByTrackCode(String trackCode);
}
