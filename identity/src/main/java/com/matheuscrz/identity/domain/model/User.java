package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidUserDataException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class User {

    public static final int NAME_MAX_LENGTH = 120;

    private final UUID id;
    private final Email email;
    private String passwordHash;
    private String name;
    private Role role;
    private UserStatus status;
    private final Instant createdAt;

    private User(UUID id, Email email, String passwordHash, String name,
            Role role, UserStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = requireHash(passwordHash);
        this.name = requireName(name);
        this.role = Objects.requireNonNull(role, "role");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static User registerCustomer(UUID id, Email email, String passwordHash,
            String name, Instant now) {
        return new User(id, email, passwordHash, name,
                Role.CUSTOMER, UserStatus.ACTIVE, now);
    }

    public static User createAdmin(UUID id, Email email, String passwordHash,
            String name, Instant now) {
        return new User(id, email, passwordHash, name,
                Role.ADMIN, UserStatus.ACTIVE, now);
    }

    public static User restore(UUID id, Email email, String passwordHash,
            String name, Role role, UserStatus status,
            Instant createdAt) {
        return new User(id, email, passwordHash, name, role, status, createdAt);
    }

    public boolean canAuthenticate() {
        return status.canAuthenticate();
    }

    public void disable() {
        status = UserStatus.INACTIVE;
    }

    public void enable() {
        status = UserStatus.ACTIVE;
    }

    public void changeRole(Role newRole) {
        role = Objects.requireNonNull(newRole, "newRole");
    }

    public void rename(String newName) {
        name = requireName(newName);
    }

    public void changePasswordHash(String newHash) {
        passwordHash = requireHash(newHash);
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String name() {
        return name;
    }

    public Role role() {
        return role;
    }

    public UserStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private static String requireName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserDataException("O nome é obrigatório.");
        }

        String normalized = value.strip();

        if (normalized.length() > NAME_MAX_LENGTH) {
            throw new InvalidUserDataException(
                    "O nome deve ter no máximo " + NAME_MAX_LENGTH + " caracteres.");
        }

        return normalized;
    }

    private static String requireHash(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserDataException("O hash de senha é obrigatório.");
        }

        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof User other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{id=" + id
                + ", email=" + email
                + ", role=" + role
                + ", status=" + status + "}";
    }
}
