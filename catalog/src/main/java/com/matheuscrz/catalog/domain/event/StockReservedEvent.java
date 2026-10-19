package com.matheuscrz.catalog.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        List<ReservedItem> items,
        Instant occurredAt) {
    public record ReservedItem(UUID productId, int quantity) {
    }
}