package com.easemybooking.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Values come from application.yml under "auth.invite.*" */
//@Component
@ConfigurationProperties(prefix = "auth.invite")
public record RoleInviteProperties(String admin, String owner) {}
