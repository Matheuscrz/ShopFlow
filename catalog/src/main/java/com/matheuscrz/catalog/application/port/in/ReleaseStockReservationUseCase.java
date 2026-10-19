package com.matheuscrz.catalog.application.port.in;

import java.util.UUID;

public interface ReleaseStockReservationUseCase {
    void release(UUID orderId, String correlationId);
}