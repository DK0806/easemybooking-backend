package com.easemybooking.places.controller;

import com.easemybooking.places.dto.PlaceRequest;
import com.easemybooking.places.dto.PlaceResponse;
import com.easemybooking.places.security.annotations.IsOwnerOrAdmin;
import com.easemybooking.places.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/places") // align with SecurityConfig
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService service;

    // OWNER or ADMIN can create a place (ownerId taken from JWT in service)
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PostMapping
    public ResponseEntity<PlaceResponse> create(@Valid @RequestBody PlaceRequest request) {
        PlaceResponse created = service.create(request);
        // If your DTO field is 'placeId', use getPlaceId(); if it's 'id', use getId()
        UUID pid = created.getPlaceId();
        return ResponseEntity.created(URI.create("/api/v1/places/" + pid)).body(created);
    }

    // Public read
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> getOne(@PathVariable("placeId") UUID placeId) {
        return ResponseEntity.ok(service.get(placeId));
    }

    // Public list with paging/sorting
    @GetMapping
    public ResponseEntity<Page<PlaceResponse>> list(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        // parse "field,dir"
        String[] parts = sort.split(",", 2);
        String sortField = parts[0].trim().isEmpty() ? "name" : parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));
        return ResponseEntity.ok(service.list(city, pageable));
    }

    // OWNER (of the resource) or ADMIN can update
    @IsOwnerOrAdmin
    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> update(@P("placeId") @PathVariable("placeId") UUID placeId,
                                                @Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.ok(service.update(placeId, request));
    }

    // OWNER (of the resource) or ADMIN can delete
    @IsOwnerOrAdmin
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(@P("placeId") @PathVariable("placeId") UUID placeId) {
        service.delete(placeId);
        return ResponseEntity.noContent().build();
    }
}
