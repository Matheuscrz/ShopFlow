package com.matheuscrz.catalog.application.port.in;

import java.util.List;
import java.util.UUID;

public interface ReserveStockUseCase {

    void reserve(ReserveStockCommand command);

    record ReserveStockCommand(
            UUID orderId,
            String correlationId,
            List<ItemToReserve> items
    ) {
        public record ItemToReserve(UUID productId, int quantity) {}
    }
}