package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;
import com.matheuscrz.catalog.domain.exception.InsufficientStockException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ProductInventory {

    private final UUID productId;
    private int availableStock;
    private Instant updatedAt;

    public ProductInventory(UUID productId, int availableStock, Instant updatedAt) {
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        if (availableStock < 0) {
            throw new DomainException("Available stock cannot be negative: " + availableStock);
        }
        this.availableStock = availableStock;
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, Instant::now);
    }

    public static ProductInventory initialize(UUID productId, int initialStock) {
        return new ProductInventory(productId, initialStock, Instant.now());
    }

    public void adjustStock(int newStock) {
        if (newStock < 0) {
            throw new DomainException("New stock level cannot be negative: " + newStock);
        }
        this.availableStock = newStock;
        this.updatedAt = Instant.now();
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("Reserve quantity must be positive");
        }
        if (this.availableStock < quantity) {
            throw new InsufficientStockException(productId, quantity, this.availableStock);
        }
        this.availableStock -= quantity;
        this.updatedAt = Instant.now();
    }

    public void release(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("Release quantity must be positive");
        }
        this.availableStock += quantity;
        this.updatedAt = Instant.now();
    }

    public UUID getProductId() {
        return productId;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}