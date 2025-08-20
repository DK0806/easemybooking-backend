package com.easemybooking.places.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceResponse {
    private UUID placeId;
    private String name;
    private String city;
    private String description;
    private Integer capacity;
    private BigDecimal pricePerSlot;
    private UUID ownerId;
    private Instant createdAt;
    private Instant updatedAt;
}
