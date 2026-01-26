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

    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    private Stage stage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime eventTime;

    private String comment;

    @PrePersist
    public void prePersist() {
        eventTime = LocalDateTime.now();
    }
}
