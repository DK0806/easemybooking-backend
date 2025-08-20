package com.easemybooking.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID placeId,
        @NotNull LocalDate forDate,
        @Min(1) int quantity
) {}
