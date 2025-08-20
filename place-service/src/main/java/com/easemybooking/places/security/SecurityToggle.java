package com.easemybooking.places.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("securityToggle")
public class SecurityToggle {
    @Value("${app.security.disabled:false}")
    private boolean disabled;

    public boolean permitAll() {
        return disabled;
    }
}
