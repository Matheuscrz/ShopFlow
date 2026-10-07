package com.matheuscrz.identity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataUserAddressJpaRepository extends JpaRepository<UserAddressJpaEntity, UUID> {
    Optional<UserAddressJpaEntity> findByIdAndUserId(UUID id, UUID userId);
    List<UserAddressJpaEntity> findAllByUserId(UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}