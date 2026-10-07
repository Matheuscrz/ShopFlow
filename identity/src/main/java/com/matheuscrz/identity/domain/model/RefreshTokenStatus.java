package com.matheuscrz.identity.domain.model;

public enum RefreshTokenStatus {
    ACTIVE,
    USED,
    REVOKED,
    EXPIRED;

    public boolean canTransitionTo(RefreshTokenStatus newStatus) {
        switch (this) {
            case ACTIVE:
                return newStatus == USED || newStatus == REVOKED || newStatus == EXPIRED;
            case USED:
                return newStatus == REVOKED || newStatus == EXPIRED;
            case REVOKED:
                return newStatus == EXPIRED;
            case EXPIRED:
                return false;
            default:
                return false;
        }
    }
}
