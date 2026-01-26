package com.example.aemix.repository;

import com.example.aemix.entity.ShipmentEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipmentEventsRepository extends JpaRepository<ShipmentEvents, UUID> {
}
