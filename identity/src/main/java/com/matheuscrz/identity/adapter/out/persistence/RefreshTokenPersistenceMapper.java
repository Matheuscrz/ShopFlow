package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.RefreshToken;

public final class RefreshTokenPersistenceMapper {

    private RefreshTokenPersistenceMapper() {
    }

    static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getFamilyId(),
                entity.getStatus(),
                entity.getExpiresAt(),
                entity.getReplacedBy(),
                entity.getCreatedAt());
    }

    static RefreshTokenJpaEntity toEntity(RefreshToken token) {
        return new RefreshTokenJpaEntity(
                token.id(),
                token.userId(),
                token.tokenHash(),
                token.familyId(),
                token.status(),
                token.expiresAt(),
                token.replacedBy(),
                token.createdAt());
    }
}
