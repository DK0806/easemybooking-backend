package com.easemybooking.places.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull @Min(value = 1, message = "capacity must be >= 1")
    private Integer capacity;

    @NotNull @DecimalMin(value = "0.0", inclusive = true, message = "pricePerSlot must be >= 0")
    private BigDecimal pricePerSlot;

    private UUID ownerId; // optional now
}
