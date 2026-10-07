package com.matheuscrz.identity.domain.model;

/**
 * Enum que representa os papéis de usuário no sistema.
 * Role
 */
public enum Role {
    CUSTOMER,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
