// service/UserService.java
package com.easemybooking.auth.service;

import com.easemybooking.auth.dto.*;
import com.easemybooking.auth.entity.UserEntity;
import java.util.UUID;

public interface UserService {
    UserEntity register(RegisterRequest req);
    UserEntity get(UUID id);
    UserEntity findByEmailOrThrow(String email);
    void grantRole(UUID userId, String role);
    void revokeRole(UUID userId, String role);
    void updateStatus(UUID userId, Boolean enabled, Boolean locked);
}
