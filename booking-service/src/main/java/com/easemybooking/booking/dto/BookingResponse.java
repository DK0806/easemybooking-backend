package com.easemybooking.booking.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID placeId,
        LocalDate forDate,
        int quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {}
