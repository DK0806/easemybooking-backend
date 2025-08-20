package com.easemybooking.places.service.impl;

import com.easemybooking.places.dto.PlaceRequest;
import com.easemybooking.places.dto.PlaceResponse;
import com.easemybooking.places.exception.PlaceNotFoundException;
import com.easemybooking.places.mapper.PlaceMapper;
import com.easemybooking.places.model.Place;
import com.easemybooking.places.repository.PlaceRepository;
import com.easemybooking.places.security.AuthUtils; // <-- ensure this class exists as provided earlier
import com.easemybooking.places.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository repo;

    @Override
    @Transactional
    public PlaceResponse create(PlaceRequest req) {
        // Prefer ownerId from JWT; ignore client-supplied value if token present
//        UUID ownerId = AuthUtils.currentUserId()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required"));
//        AuthUtils.currentUserId().ifPresent(req::setOwnerId);

        // Soft duplicate guard (unique index is the hard guard)
        if (repo.existsByNameIgnoreCaseAndCityIgnoreCase(req.getName(), req.getCity())) {
            log.warn("Duplicate place attempt: name='{}', city='{}'", req.getName(), req.getCity());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A place with the same name already exists in this city");
        }

        Place entity = PlaceMapper.toEntity(req);
        //entity.setOwnerId(ownerId);
        try {
            entity = repo.save(entity);
        } catch (DuplicateKeyException e) {
            // In case the unique index on placeId or (name, city) trips
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate place", e);
        }

        log.info("Created place {}", entity.getPlaceId());
        return PlaceMapper.toResponse(entity);
    }

    @Override
    public PlaceResponse get(UUID placeId) {
        Place p = repo.findByPlaceId(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found: " + placeId));
        return PlaceMapper.toResponse(p);
    }

    @Override
    public Page<PlaceResponse> list(String city, Pageable pageable) {
        Page<Place> page = (city == null || city.isBlank())
                ? repo.findAll(pageable)
                : repo.findByCityIgnoreCase(city.trim(), pageable);
        return page.map(PlaceMapper::toResponse);
    }

    @Override
    @Transactional
    public PlaceResponse update(UUID placeId, PlaceRequest req) {
        Place p = repo.findByPlaceId(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found: " + placeId));

        // Ownership / role enforcement
        boolean isAdmin = AuthUtils.hasRole("ADMIN");
        UUID requester = AuthUtils.currentUserId().orElse(null);
        if (!isAdmin) {
            if (p.getOwnerId() == null || requester == null || !p.getOwnerId().equals(requester)) {
                log.warn("Unauthorized update attempt by {} on place {}", requester, placeId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to modify this place");
            }
        }

        // Prevent accidental (name, city) duplicates on update
        if (!p.getName().equalsIgnoreCase(req.getName().trim())
                || !p.getCity().equalsIgnoreCase(req.getCity().trim())) {
            if (repo.existsByNameIgnoreCaseAndCityIgnoreCase(req.getName(), req.getCity())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A place with the same name already exists in this city");
            }
        }

        PlaceMapper.update(p, req);
        p = repo.save(p);
        log.info("Updated place {}", p.getPlaceId());
        return PlaceMapper.toResponse(p);
    }

    @Override
    @Transactional
    public void delete(UUID placeId) {
        Place p = repo.findByPlaceId(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found: " + placeId));

        boolean isAdmin = AuthUtils.hasRole("ADMIN");
        UUID requester = AuthUtils.currentUserId().orElse(null);
        if (!isAdmin) {
            if (p.getOwnerId() == null || requester == null || !p.getOwnerId().equals(requester)) {
                log.warn("Unauthorized delete attempt by {} on place {}", requester, placeId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this place");
            }
        }

        // src/main/java/com/easemybooking/places/service/impl/PlaceServiceImpl.java





        repo.delete(p);
        log.info("Deleted place {}", placeId);
    }

    @Override
    public UUID getOwnerId(UUID placeId) {
        return repo.findByPlaceId(placeId)
                .map(p -> p.getOwnerId())
                .orElseThrow(() -> new PlaceNotFoundException("Place not found: " + placeId));
    }

}
