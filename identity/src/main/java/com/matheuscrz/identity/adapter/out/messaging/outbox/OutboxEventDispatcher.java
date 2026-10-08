package com.matheuscrz.identity.adapter.out.messaging.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventJpaEntity;
import com.matheuscrz.identity.adapter.out.persistence.outbox.SpringDataOutboxJpaRepository;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventDispatcher {

    private static final String TOPIC = "identity.user-events";
    private final SpringDataOutboxJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${app.outbox.poller-interval-ms:5000}")
    @Transactional
    public void processOutbox() {
        List<OutboxEventJpaEntity> batch = outboxRepository.findPendingEventsForUpdate(50);

        for (OutboxEventJpaEntity event : batch) {
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        TOPIC,
                        event.getAggregateId().toString(),
                        event.getPayload());

                record.headers().add(
                        new RecordHeader("eventType", event.getEventType().name().getBytes(StandardCharsets.UTF_8)));
                record.headers()
                        .add(new RecordHeader("eventId", event.getId().toString().getBytes(StandardCharsets.UTF_8)));

                // Envio síncrono para garantir entrega antes de comitar a transação
                kafkaTemplate.send(record).get(3, TimeUnit.SECONDS);
                event.markPublished(clock);

            } catch (Exception ex) {
                log.error("Falha ao despachar outbox event {}", event.getId(), ex);
                event.registerFailure(ex.getMessage());
            }
        }
    }

    // Expurgo automático de eventos já publicados
    @Scheduled(cron = "${app.outbox.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void purgePublishedEvents() {
        Instant retentionLimit = Instant.now().minus(7, ChronoUnit.DAYS);
        int deleted = outboxRepository.deletePublishedBefore(retentionLimit);
        log.info("Expurgo do Outbox: {} eventos antigos removidos.", deleted);
    }
}