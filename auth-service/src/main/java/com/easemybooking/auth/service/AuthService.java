// service/AuthService.java
package com.easemybooking.auth.service;

import com.easemybooking.auth.dto.*;

public interface AuthService {
    AuthResponse login(LoginRequest req);
}
