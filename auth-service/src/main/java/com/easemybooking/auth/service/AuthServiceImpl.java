// service/AuthServiceImpl.java
package com.easemybooking.auth.service;

import com.easemybooking.auth.dto.*;
import com.easemybooking.auth.entity.UserEntity;
import com.easemybooking.auth.exception.InvalidCredentialsException;
import com.easemybooking.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Override
    public AuthResponse login(LoginRequest req) {
        UserEntity user = userService.findByEmailOrThrow(req.email());
        if (user.isLocked() || !user.isEnabled() || !encoder.matches(req.password(), user.getPassword()))
            throw new InvalidCredentialsException();

        var token = jwt.generateAccessToken(user);

        var roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        return AuthResponse.of(token, 30*60, user.getId(), user.getEmail(), user.getFullName(), roles);
    }
}
