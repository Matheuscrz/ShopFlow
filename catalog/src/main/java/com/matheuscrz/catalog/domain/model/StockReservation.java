package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class StockReservation {

    private final UUID id;
    private final UUID orderId;
    private final UUID productId;
    private final int quantity;
    private ReservationStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public StockReservation(UUID id, UUID orderId, UUID productId, int quantity,
                            ReservationStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Reservation ID cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        if (quantity <= 0) {
            throw new DomainException("Reservation quantity must be strictly positive");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNullElse(status, ReservationStatus.RESERVED);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, Instant::now);
    }

    public static StockReservation create(UUID orderId, UUID productId, int quantity) {
        Instant now = Instant.now();
        return new StockReservation(UUID.randomUUID(), orderId, productId, quantity, ReservationStatus.RESERVED, now, now);
    }

    public void confirm() {
        if (this.status != ReservationStatus.RESERVED) {
            throw new DomainException("Cannot confirm reservation in status " + this.status);
        }
        this.status = ReservationStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void release() {
        if (this.status == ReservationStatus.RELEASED) {
            return; // Idempotente
        }
        if (this.status != ReservationStatus.RESERVED) {
            throw new DomainException("Cannot release reservation in status " + this.status);
        }
        this.status = ReservationStatus.RELEASED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public UUID getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public ReservationStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}