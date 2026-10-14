package com.matheuscrz.identity.adapter.out.persistence.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SpringDataOutboxJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    @Query(value = """
        SELECT * FROM outbox_events 
        WHERE status = 'PENDING' 
        ORDER BY created_at ASC 
        LIMIT :limit 
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEventJpaEntity> findPendingEventsForUpdate(@Param("limit") int limit);

    @Modifying
    @Query("DELETE FROM OutboxEventJpaEntity o WHERE o.status = 'PUBLISHED' AND o.createdAt < :cutoff")
    int deletePublishedBefore(@Param("cutoff") Instant cutoff);
}