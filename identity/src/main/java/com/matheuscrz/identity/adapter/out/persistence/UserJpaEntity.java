package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    protected UserJpaEntity() {
    }

    UserJpaEntity(UUID id, String email, String passwordHash, String name,
            Role role, UserStatus status) {
        super(id);
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    String getEmail() {
        return email;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    String getName() {
        return name;
    }

    Role getRole() {
        return role;
    }

    UserStatus getStatus() {
        return status;
    }

    void applyChanges(User user) {
        this.name = user.name();
        this.passwordHash = user.passwordHash();
        this.role = user.role();
        this.status = user.status();
    }
}
