// dto/RoleUpdateRequest.java
package com.easemybooking.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleUpdateRequest(@NotBlank String role) {}
