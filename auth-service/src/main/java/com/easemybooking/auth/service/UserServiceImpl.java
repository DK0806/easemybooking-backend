// service/UserServiceImpl.java
package com.easemybooking.auth.service;

import com.easemybooking.auth.dto.*;
import com.easemybooking.auth.entity.*;
import com.easemybooking.auth.exception.DuplicateResourceException;
import com.easemybooking.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    @Override
    public UserEntity register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) throw new DuplicateResourceException("Email already registered");
        var userRole = roles.findByName("USER").orElseThrow();
        var user = UserEntity.builder()
                .email(req.email())
                .password(encoder.encode(req.password()))
                .fullName(req.fullName())
                .roles(new HashSet<>(Set.of(userRole)))
                .enabled(true).locked(false)
                .build();
        return users.save(user);
    }

    @Override public UserEntity get(UUID id){ return users.findById(id).orElseThrow(); }
    @Override public UserEntity findByEmailOrThrow(String email){ return users.findByEmail(email).orElseThrow(); }

    @Override public void grantRole(UUID userId, String role){
        var user = get(userId);
        var r = roles.findByName(role).orElseThrow();
        user.getRoles().add(r);
    }
    @Override public void revokeRole(UUID userId, String role){
        var user = get(userId);
        user.getRoles().removeIf(rr -> rr.getName().equals(role));
    }
    @Override public void updateStatus(UUID userId, Boolean enabled, Boolean locked){
        var u = get(userId);
        if (enabled != null) u.setEnabled(enabled);
        if (locked != null)  u.setLocked(locked);
    }
}
