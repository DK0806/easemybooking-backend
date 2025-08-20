package com.easemybooking.places.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

/**
 * Use when the method has a 'placeId' param of type UUID.
 * It delegates ownership check to @placeAuth.canModify(#placeId).
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@placeAuth.canModify(#placeId)")
//@PreAuthorize("@securityToggle.permitAll() or hasAnyRole('OWNER','ADMIN')")
public @interface IsOwnerOrAdmin {}
