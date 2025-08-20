package com.easemybooking.booking.service;

import com.easemybooking.booking.client.PlaceClient;
import com.easemybooking.booking.dto.BookingResponse;
import com.easemybooking.booking.dto.CreateBookingRequest;
import com.easemybooking.booking.entity.*;
import com.easemybooking.booking.exception.CapacityExceededException;
import com.easemybooking.booking.repository.*;
//import com.easemybooking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final DailyInventoryRepository inventoryRepo;
    private final PlaceClient placeClient;

    @Override
    @Transactional
    public BookingResponse create(UUID userId, CreateBookingRequest req) {
        var place = placeClient.getPlace(req.placeId());
        // Ensure daily inventory row exists (capacity comes from place-service on first use)
        inventoryRepo.findById(new DailyInventoryId(req.placeId(), req.forDate()))
                .orElseGet(() -> inventoryRepo.save(DailyInventoryEntity.builder()
                        .placeId(req.placeId())
                        .forDate(req.forDate())
                        .capacity(place.capacity() != null ? place.capacity() : 0)
                        .reserved(0)
                        .build()));

        int updated = inventoryRepo.tryReserve(req.placeId(), req.forDate(), req.quantity());
        if (updated != 1) {
            throw new CapacityExceededException("Not enough capacity remaining for that date");
        }

        BigDecimal price = place.pricePerSlot() != null ? place.pricePerSlot() : BigDecimal.ZERO;
        BigDecimal total = price.multiply(BigDecimal.valueOf(req.quantity()));

        var saved = bookingRepo.save(BookingEntity.builder()
                .userId(userId)
                .placeId(req.placeId())
                .forDate(req.forDate())
                .quantity(req.quantity())
                .pricePerUnit(price)
                .totalAmount(total)
                .status("CREATED")
                .build());

        return toDto(saved);
    }

    @Override
    @Transactional
    public void cancel(UUID userId, UUID bookingId) {
        var b = bookingRepo.findById(bookingId).orElseThrow();
        // allow only owner or admin (admin is enforced at controller via @PreAuthorize)
        if (!b.getUserId().equals(userId)) {
            throw new SecurityException("Not your booking");
        }
        if (!"CREATED".equals(b.getStatus())) return;

        b.setStatus("CANCELED");
        bookingRepo.save(b);
        inventoryRepo.release(b.getPlaceId(), b.getForDate(), b.getQuantity());
    }

    @Override
    public List<BookingResponse> listMine(UUID userId) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    private BookingResponse toDto(BookingEntity e) {
        return new BookingResponse(e.getId(), e.getPlaceId(), e.getForDate(), e.getQuantity(),
                e.getPricePerUnit(), e.getTotalAmount(), e.getStatus(), e.getCreatedAt());
    }
}
