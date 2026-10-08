package com.matheuscrz.identity.adapter.out.persistence.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface SpringDataConsumedEventJpaRepository extends JpaRepository<ConsumedEventJpaEntity, UUID> {

    @Modifying
    @Query("DELETE FROM ConsumedEventJpaEntity c WHERE c.receivedAt < :cutoff")
    int deleteOldConsumedEvents(@Param("cutoff") Instant cutoff);
}