package com.example.aemix.repositories;

import com.example.aemix.entities.Order;
import com.example.aemix.entities.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    
    Optional<Order> findByTrackCode(String trackCode);
    
    List<Order> findByCityIdAndStatus(Long cityId, Status status);
    
    List<Order> findByTrackCodeInAndStatus(List<String> trackCodes, Status status);

    @Query("""
    SELECT o FROM Order o
    LEFT JOIN o.city c
    WHERE (CAST(:trackCode AS string) IS NULL OR LOWER(o.trackCode) LIKE LOWER(CONCAT(CAST(:trackCode AS string), '%')))
      AND (CAST(:status AS string) IS NULL OR o.status = :status)
      AND (CAST(:cityId AS long) IS NULL OR c.id = :cityId)
      AND (CAST(:fromDate AS localdatetime) IS NULL OR o.createdAt >= :fromDate)
      AND (CAST(:toDate AS localdatetime) IS NULL OR o.createdAt <= :toDate)
    """)
    Page<Order> findOrders(
            @Param("trackCode") String trackCode,
            @Param("status") Status status,
            @Param("cityId") Long cityId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
