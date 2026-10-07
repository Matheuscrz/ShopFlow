package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidRefreshTokenException;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final UUID familyId;
    private RefreshTokenStatus status;
    private final Instant expiresAt;
    private UUID replacedBy;
    private final Instant createdAt;

    private RefreshToken(UUID id, UUID userId, String tokenHash, UUID familyId,
            RefreshTokenStatus status, Instant expiresAt,
            UUID replacedBy, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.tokenHash = requireTokenHash(tokenHash);
        this.familyId = Objects.requireNonNull(familyId, "familyId");
        this.status = Objects.requireNonNull(status, "status");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.replacedBy = replacedBy;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static RefreshToken issue(UUID id, UUID userId, String tokenHash,
            UUID familyId, Instant now, Duration ttl) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(ttl, "ttl");

        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl deve ser positivo");
        }

        return new RefreshToken(
                id,
                userId,
                tokenHash,
                familyId,
                RefreshTokenStatus.ACTIVE,
                now.plus(ttl),
                null,
                now);
    }

    public static RefreshToken restore(UUID id, UUID userId, String tokenHash,
            UUID familyId, RefreshTokenStatus status,
            Instant expiresAt, UUID replacedBy,
            Instant createdAt) {
        return new RefreshToken(
                id, userId, tokenHash, familyId, status,
                expiresAt, replacedBy, createdAt);
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isReuseAttempt() {
        return status == RefreshTokenStatus.USED;
    }

    public void assertUsable(Instant now) {
        if (status != RefreshTokenStatus.ACTIVE || isExpired(now)) {
            throw new InvalidRefreshTokenException();
        }
    }

    public void markUsed(UUID replacedBy, Instant now) {
        assertUsable(now);
        this.replacedBy = Objects.requireNonNull(replacedBy, "replacedBy");
        status = RefreshTokenStatus.USED;
    }

    public void revoke() {
        if (status.canTransitionTo(RefreshTokenStatus.REVOKED)) {
            status = RefreshTokenStatus.REVOKED;
        }
    }

    public void markExpired(Instant now) {
        if (status == RefreshTokenStatus.ACTIVE && isExpired(now)) {
            status = RefreshTokenStatus.EXPIRED;
        }
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String tokenHash() {
        return tokenHash;
    }

    public UUID familyId() {
        return familyId;
    }

    public RefreshTokenStatus status() {
        return status;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public UUID replacedBy() {
        return replacedBy;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private static String requireTokenHash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tokenHash é obrigatório");
        }

        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof RefreshToken other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
