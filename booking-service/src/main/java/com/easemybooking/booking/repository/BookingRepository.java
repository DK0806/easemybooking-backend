package com.easemybooking.booking.repository;

import com.easemybooking.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    List<BookingEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
