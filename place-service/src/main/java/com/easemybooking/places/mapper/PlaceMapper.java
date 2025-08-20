package com.easemybooking.places.mapper;

import com.easemybooking.places.dto.PlaceRequest;
import com.easemybooking.places.dto.PlaceResponse;
import com.easemybooking.places.model.Place;

import java.time.Instant;
import java.util.UUID;

public final class PlaceMapper {
    private PlaceMapper() {}

    public static Place toEntity(PlaceRequest r) {
        return Place.builder()
                .placeId(UUID.randomUUID())
                .name(r.getName().trim())
                .city(r.getCity().trim())
                .description(r.getDescription().trim())
                .capacity(r.getCapacity())
                .pricePerSlot(r.getPricePerSlot())
                .ownerId(r.getOwnerId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static void update(Place p, PlaceRequest r) {
        p.setName(r.getName().trim());
        p.setCity(r.getCity().trim());
        p.setDescription(r.getDescription().trim());
        p.setCapacity(r.getCapacity());
        p.setPricePerSlot(r.getPricePerSlot());
        //if (r.getOwnerId() != null) p.setOwnerId(r.getOwnerId());
        p.setUpdatedAt(Instant.now());
    }

    public static PlaceResponse toResponse(Place p) {
        return PlaceResponse.builder()
                .placeId(p.getPlaceId())
                .name(p.getName())
                .city(p.getCity())
                .description(p.getDescription())
                .capacity(p.getCapacity())
                .pricePerSlot(p.getPricePerSlot())
                .ownerId(p.getOwnerId())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
