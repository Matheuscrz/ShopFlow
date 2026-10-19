package com.matheuscrz.catalog.application.port.out;

import com.matheuscrz.catalog.domain.model.StockReservation;

import java.util.List;
import java.util.UUID;

public interface StockReservationRepositoryPort {
    StockReservation save(StockReservation reservation);

    List<StockReservation> saveAll(List<StockReservation> reservations);

    List<StockReservation> findAllByOrderId(UUID orderId);
}