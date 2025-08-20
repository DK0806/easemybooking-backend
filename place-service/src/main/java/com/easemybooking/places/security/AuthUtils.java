//package com.easemybooking.places.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public final class AuthUtils {
//    private AuthUtils() {}
//
//    public static Optional<UUID> currentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) return Optional.empty();
//        Object sub = jwt.getClaims().get("sub");
//        if (sub == null) return Optional.empty();
//        try { return Optional.of(UUID.fromString(sub.toString())); }
//        catch (IllegalArgumentException e) { return Optional.empty(); }
//    }
//
//    public static boolean hasRole(String role) {
//        Authentication a = SecurityContextHolder.getContext().getAuthentication();
//        return a != null && a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_" + role));
//    }
//}


package com.easemybooking.places.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

public final class AuthUtils {
    private AuthUtils() {}

    public static Optional<UUID> currentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a instanceof JwtAuthenticationToken jwt) {
            try { return Optional.of(UUID.fromString(jwt.getToken().getSubject())); }
            catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    public static boolean hasRole(String role) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return false;
        String needed = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        for (GrantedAuthority ga : a.getAuthorities()) {
            if (needed.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}

