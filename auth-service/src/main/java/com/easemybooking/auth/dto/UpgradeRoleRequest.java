package com.easemybooking.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record UpgradeRoleRequest(
        @NotBlank String role,       // "OWNER" or "ADMIN" (case-insensitive)
        @NotBlank String inviteCode  // secret invite code
) {}
