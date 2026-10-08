package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.application.port.out.RefreshTokenRepositoryPort;
import com.matheuscrz.identity.domain.model.RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenJpaRepository repository;

    public RefreshTokenPersistenceAdapter(SpringDataRefreshTokenJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = repository.findById(refreshToken.id())
                .map(existing -> {
                    existing.applyChanges(refreshToken);
                    return existing;
                })
                .orElseGet(() -> RefreshTokenPersistenceMapper.toEntity(refreshToken));
        return RefreshTokenPersistenceMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(RefreshTokenPersistenceMapper::toDomain);
    }

    @Override
    public void revokeAllByFamilyId(UUID familyId) {
        repository.revokeActiveTokensByFamilyId(familyId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamilyOnReuseDetection(UUID familyId) {
        repository.revokeActiveTokensByFamilyId(familyId);
    }
}