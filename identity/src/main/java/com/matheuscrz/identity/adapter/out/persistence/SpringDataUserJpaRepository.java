package com.matheuscrz.identity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserJpaRepository
        extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailLookupHash(String emailLookupHash);

    Optional<UserJpaEntity> findByCpfLookupHash(String cpfLookupHash);

    boolean existsByEmailLookupHash(String emailLookupHash);

    boolean existsByCpfLookupHash(String cpfLookupHash);
}