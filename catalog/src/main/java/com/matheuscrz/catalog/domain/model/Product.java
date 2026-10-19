package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;
import com.matheuscrz.catalog.domain.exception.ProductInactiveException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Product {

    private final UUID id;
    private UUID categoryId;
    private final Sku sku;
    private String name;
    private String description;
    private Money price;
    private ProductStatus status;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public Product(UUID id, UUID categoryId, Sku sku, String name, String description,
            Money price, ProductStatus status, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Product ID cannot be null");
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID cannot be null");
        this.sku = Objects.requireNonNull(sku, "SKU cannot be null");
        this.setName(name);
        this.description = description != null ? description.trim() : null;
        this.setPrice(price);
        this.status = Objects.requireNonNullElse(status, ProductStatus.ACTIVE);
        this.version = version;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, Instant::now);
    }

    public static Product create(UUID categoryId, Sku sku, String name, String description, Money price) {
        Instant now = Instant.now();
        return new Product(UUID.randomUUID(), categoryId, sku, name, description, price, ProductStatus.ACTIVE, 0L, now,
                now);
    }

    public void updateDetails(String name, String description, UUID categoryId) {
        setName(name);
        this.description = description != null ? description.trim() : null;
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID cannot be null");
        this.updatedAt = Instant.now();
    }

    public void changePrice(Money newPrice) {
        setPrice(newPrice);
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void inactivate() {
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void ensureActiveForSale() {
        if (this.status != ProductStatus.ACTIVE) {
            throw new ProductInactiveException("Product " + this.id + " is not active for sale");
        }
    }

    private void setName(String name) {
        if (name == null || name.isBlank() || name.trim().length() < 2 || name.trim().length() > 255) {
            throw new DomainException("Product name must be between 2 and 255 characters");
        }
        this.name = name.trim();
    }

    private void setPrice(Money price) {
        Objects.requireNonNull(price, "Price cannot be null");
        if (price.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new DomainException("Product price must be strictly greater than zero");
        }
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public Sku getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}