package com.matheuscrz.catalog.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockRejectedEvent(
        UUID orderId,
        String reason,
        List<UUID> unavailableProductIds,
        Instant occurredAt) {
}