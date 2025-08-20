package com.easemybooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name="bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingEntity {
    @Id @Column(columnDefinition="uuid") private UUID id;

    @PrePersist void pre() {
        if (id == null) id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }
    @PreUpdate void upd() { updatedAt = Instant.now(); }

    @Column(name="user_id",  nullable=false, columnDefinition="uuid") private UUID userId;
    @Column(name="place_id", nullable=false, columnDefinition="uuid") private UUID placeId;
    @Column(name="for_date", nullable=false) private LocalDate forDate;
    @Column(nullable=false) private Integer quantity;

    @Column(name="price_per_unit", nullable=false, precision=10, scale=2) private BigDecimal pricePerUnit;
    @Column(name="total_amount",   nullable=false, precision=12, scale=2) private BigDecimal totalAmount;

    @Column(nullable=false, length=16) private String status; // CREATED | CANCELED

    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
}
