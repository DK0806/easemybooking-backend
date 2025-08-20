// security/PlaceAuth.java
package com.easemybooking.places.security;

import com.easemybooking.places.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("placeAuth")
@RequiredArgsConstructor
public class PlaceAuth {
    private final PlaceRepository placeRepository;
    private final SecurityToggle securityToggle;

    public boolean canModify(UUID placeId) {
        if (securityToggle.permitAll()) return true;          // dev mode bypass
        if (AuthUtils.hasRole("ADMIN")) return true;          // admins can do everything
        var current = AuthUtils.currentUserId().orElse(null);
        if (current == null) return false;

        return placeRepository.findByPlaceId(placeId)
                .map(p -> current.equals(p.getOwnerId()))
                .orElse(false);
    }
}
