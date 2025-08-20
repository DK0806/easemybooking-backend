package com.easemybooking.places.model;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
public class UserInfo {

    private String id;
    //private String name;
    private String email;
    private String password;
    private List<SimpleGrantedAuthority> roles;
}
