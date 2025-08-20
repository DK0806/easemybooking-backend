// controller/AdminController.java
package com.easemybooking.auth.controller;

import com.easemybooking.auth.dto.*;
import com.easemybooking.auth.entity.UserEntity;
import com.easemybooking.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping("/users/{id}")
    public Map<String,Object> get(@PathVariable("id") UUID id) {
        UserEntity u = userService.get(id);
        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "name", u.getFullName(),
                "roles", u.getRoles().stream().map(r -> r.getName()).toList(),
                "enabled", u.isEnabled(),
                "locked", u.isLocked()
        );
    }

    @PatchMapping("/users/{id}/roles/grant")
    public void grant(@PathVariable("id") UUID id, @Valid @RequestBody RoleUpdateRequest req) { userService.grantRole(id, req.role()); }
    @PatchMapping("/users/{id}/roles/revoke")
    public void revoke(@PathVariable("id") UUID id, @Valid @RequestBody RoleUpdateRequest req) { userService.revokeRole(id, req.role()); }
    @PatchMapping("/users/{id}/status")
    public void status(@PathVariable("id") UUID id, @RequestBody UserStatusRequest req) { userService.updateStatus(id, req.enabled(), req.locked()); }
}
