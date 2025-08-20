package com.easemybooking.places.service;

import com.easemybooking.places.dto.PlaceRequest;
import com.easemybooking.places.dto.PlaceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PlaceService {
    PlaceResponse create(PlaceRequest request);
    public PlaceResponse get(UUID placeId);
    Page<PlaceResponse> list(String city, Pageable pageable);
    PlaceResponse update(UUID placeId, PlaceRequest request);
    void delete(UUID placeId);
    UUID getOwnerId(UUID placeId);

}
