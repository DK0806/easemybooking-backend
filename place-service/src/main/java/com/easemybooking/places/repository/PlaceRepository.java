package com.easemybooking.places.repository;

import com.easemybooking.places.model.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlaceRepository extends MongoRepository<Place, String> {
    Optional<Place> findByPlaceId(UUID placeId);
    Page<Place> findByCityIgnoreCase(String city, Pageable pageable);
    boolean existsByNameIgnoreCaseAndCityIgnoreCase(String name, String city);
}
