// entity/UserEntity.java
package com.easemybooking.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {
    @Id @Column(columnDefinition="uuid")
    private UUID id;

    @PrePersist void pre() { if (id == null) id = UUID.randomUUID(); createdAt = updatedAt = Instant.now(); }
    @PreUpdate  void upd() { updatedAt = Instant.now(); }

    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false) private String password;
    private String fullName;
    @Column(nullable=false) private boolean enabled = true;
    @Column(nullable=false) private boolean locked  = false;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private Instant updatedAt;
}
