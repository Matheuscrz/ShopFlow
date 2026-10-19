package com.matheuscrz.catalog.application.port.in;

import java.util.UUID;

public interface ConfirmStockReservationUseCase {
    void confirm(UUID orderId, String correlationId);
}