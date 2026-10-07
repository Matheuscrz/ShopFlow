package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.RefreshToken;
import com.matheuscrz.identity.domain.model.RefreshTokenStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity extends BaseJpaEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefreshTokenStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    protected RefreshTokenJpaEntity() {
    }

    RefreshTokenJpaEntity(UUID id, UUID userId, String tokenHash,
            UUID familyId, RefreshTokenStatus status,
            Instant expiresAt, UUID replacedBy,
            Instant createdAt) {
        super(id);
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.replacedBy = replacedBy;
    }

    UUID getUserId() {
        return userId;
    }

    String getTokenHash() {
        return tokenHash;
    }

    UUID getFamilyId() {
        return familyId;
    }

    RefreshTokenStatus getStatus() {
        return status;
    }

    Instant getExpiresAt() {
        return expiresAt;
    }

    UUID getReplacedBy() {
        return replacedBy;
    }

    void applyChanges(RefreshToken token) {
        this.status = token.status();
        this.replacedBy = token.replacedBy();
    }
}
