package com.matheuscrz.identity.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataRefreshTokenJpaRepository
                extends JpaRepository<RefreshTokenJpaEntity, UUID> {

        Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

        /**
         * Revoga todos os tokens ativos de uma família.
         *
         * @param familyId O ID da família cujos tokens devem ser revogados.
         * @return O número de tokens revogados.
         */
        @Modifying(flushAutomatically = true, clearAutomatically = true)
        @Query("""
                        UPDATE RefreshTokenJpaEntity r
                           SET r.status = 'REVOKED'
                         WHERE r.familyId = :familyId
                           AND r.status = 'ACTIVE'
                        """)
        int revokeActiveTokensByFamilyId(@Param("familyId") UUID familyId);
}