package com.matheuscrz.identity.adapter.out.persistence.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumedEventsCleanupScheduler {

    private final SpringDataConsumedEventJpaRepository repository;
    private final Clock clock;

    // Executa diariamente às 03:30 para expurgar registros processados há mais de 14 dias
    @Scheduled(cron = "${app.messaging.dedup-cleanup-cron:0 30 3 * * *}")
    @Transactional
    public void purgeOldDeduplicationRecords() {
        Instant retentionLimit = Instant.now(clock).minus(14, ChronoUnit.DAYS);
        int deleted = repository.deleteOldConsumedEvents(retentionLimit);
        log.info("Expurgo de deduplicação: {} registros antigos removidos de consumed_events.", deleted);
    }
}