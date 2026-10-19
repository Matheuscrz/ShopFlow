package com.matheuscrz.catalog.domain.exception;

import java.util.UUID;

public class InsufficientStockException extends DomainException {
    private final UUID productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(UUID productId, int requested, int available) {
        super(String.format("Insufficient stock for product %s: requested %d, available %d", productId, requested,
                available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}