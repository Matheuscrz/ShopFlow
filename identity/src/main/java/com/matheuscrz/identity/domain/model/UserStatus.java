package com.matheuscrz.identity.domain.model;

/**
 * Enum que representa os status de usuário no sistema. Apenas usuários com
 * status ACTIVE podem se autenticar.
 * UserStatus
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE;

    public boolean canAuthenticate() {
        return this == ACTIVE;
    }
}
