package com.easemybooking.places.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("@securityToggle.permitAll() or hasRole('ADMIN')")
public @interface IsAdmin {}
