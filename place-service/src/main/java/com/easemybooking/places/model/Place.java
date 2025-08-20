package com.easemybooking.places.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "places")
public class Place {
    @Id
    private String id;

    @Indexed(unique = true)
    private UUID placeId;

    @Indexed
    private String name;

    @Indexed
    private String city;

    private String description;
    private Integer capacity;
    private BigDecimal pricePerSlot;

    // future: authorization/ownership
    private UUID ownerId;

    private Instant createdAt;
    private Instant updatedAt;
}
