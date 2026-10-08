package com.matheuscrz.identity.adapter.out.persistence.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOutboxJpaRepository
        extends JpaRepository<OutboxEventJpaEntity, UUID> {

    List<OutboxEventJpaEntity> findByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status,
            Pageable pageable);
}