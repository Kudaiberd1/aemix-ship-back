package com.example.aemix.entity;

import com.example.aemix.entity.enums.Stage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false, unique = true)
    private Shipments shipment;

    @Enumerated(EnumType.STRING)
    private Stage currentStage;

    @Enumerated(EnumType.STRING)
    private Stage oldStage;

    @CreatedDate
    private LocalDateTime eventTime;

    @PrePersist
    public void prePersist() {
        eventTime = LocalDateTime.now();
    }
}
