package com.easemybooking.auth.controller;

import com.easemybooking.auth.config.RoleInviteProperties;
import com.easemybooking.auth.dto.*;
import com.easemybooking.auth.entity.UserEntity;
import com.easemybooking.auth.security.JwtService;
import com.easemybooking.auth.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;
    private final RoleInviteProperties invites; // NEW

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@Valid @RequestBody RegisterRequest req) {
        UserEntity u = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", u.getId()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public Map<String,Object> me(@RequestHeader("Authorization") String authHeader) {
        var claims = jwtService.parse(authHeader.replace("Bearer ", ""));
        return Map.of(
                "userId", claims.getSubject(),
                "email", claims.get("email"),
                "name", claims.get("name"),
                "roles", claims.get("roles")
        );
    }

    /**
     * Self-upgrade: a logged-in user can become OWNER or ADMIN if they know the invite code.
     * Ensures "only after user is registered" because it requires an authenticated request.
     */
    @PostMapping("/roles/upgrade")
    @PreAuthorize("hasAnyRole('USER','OWNER','ADMIN')") // must be authenticated and at least USER
    public Map<String, Object> upgradeRole(@RequestHeader("Authorization") String authHeader,
                                           @Valid @RequestBody UpgradeRoleRequest req) {
        var claims = jwtService.parse(authHeader.replace("Bearer ", ""));
        UUID userId = UUID.fromString(claims.getSubject());

        String requested = req.role().toUpperCase();
        if (!requested.equals("OWNER") && !requested.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role must be OWNER or ADMIN");
        }

        // Validate invite code from config
        String expected = requested.equals("ADMIN") ? invites.admin() : invites.owner();
        if (expected == null || !expected.equals(req.inviteCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid invite code");
        }

        userService.grantRole(userId, requested); // idempotent in your service

        return Map.of(
                "userId", userId,
                "grantedRole", requested,
                "message", "Role granted successfully"
        );
    }
}
