package com.easemybooking.booking.service;

import com.easemybooking.booking.dto.BookingResponse;
import com.easemybooking.booking.dto.CreateBookingRequest;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponse create(UUID userId, CreateBookingRequest req);
    void cancel(UUID userId, UUID bookingId);
    List<BookingResponse> listMine(UUID userId);
}
