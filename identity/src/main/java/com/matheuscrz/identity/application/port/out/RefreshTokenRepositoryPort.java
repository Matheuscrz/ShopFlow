package com.matheuscrz.identity.application.port.out;

import java.util.Optional;
import java.util.UUID;
import com.matheuscrz.identity.domain.model.RefreshToken;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllByFamilyId(UUID familyId);
    void revokeFamilyOnReuseDetection(UUID familyId);
}