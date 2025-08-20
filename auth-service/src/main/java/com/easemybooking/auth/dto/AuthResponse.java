// dto/AuthResponse.java
package com.easemybooking.auth.dto;

import java.util.Set;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long   expiresInSeconds,
        UUID   userId,
        String email,
        String fullName,
        Set<String> roles
) {
    public static AuthResponse of(String token, long expires, UUID id, String email, String name, Set<String> roles) {
        return new AuthResponse(token, "Bearer", expires, id, email, name, roles);
    }
}
