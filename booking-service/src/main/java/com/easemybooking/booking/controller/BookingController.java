package com.easemybooking.booking.controller;

import com.easemybooking.booking.dto.*;
import com.easemybooking.booking.security.JwtService;
import com.easemybooking.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final JwtService jwtService;

    private UUID subject(String authHeader) {
        var claims = jwtService.parse(authHeader.replace("Bearer ", ""));
        return UUID.fromString(claims.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER','OWNER','ADMIN')")
    public BookingResponse create(@RequestHeader("Authorization") String auth,
                                  @Valid @RequestBody CreateBookingRequest req) {
        return bookingService.create(subject(auth), req);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','OWNER','ADMIN')")
    public List<BookingResponse> mine(@RequestHeader("Authorization") String auth) {
        return bookingService.listMine(subject(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void cancel(@RequestHeader("Authorization") String auth, @PathVariable UUID id) {
        bookingService.cancel(subject(auth), id);
    }
}
