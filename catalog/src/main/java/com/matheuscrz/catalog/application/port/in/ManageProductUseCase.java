package com.matheuscrz.catalog.application.port.in;

import com.matheuscrz.catalog.domain.model.Money;
import com.matheuscrz.catalog.domain.model.Product;
import com.matheuscrz.catalog.domain.model.Sku;

import java.util.UUID;

public interface ManageProductUseCase {

    Product createProduct(CreateProductCommand command, AuditContext audit);
    Product updateProduct(UUID id, UpdateProductCommand command, AuditContext audit);
    Product updatePrice(UUID id, Money newPrice, AuditContext audit);
    void adjustStock(UUID productId, int newStock, AuditContext audit);
    void activateProduct(UUID id, AuditContext audit);
    void inactivateProduct(UUID id, AuditContext audit);

    record CreateProductCommand(UUID categoryId, Sku sku, String name, String description, Money price, int initialStock) {}
    record UpdateProductCommand(String name, String description, UUID categoryId, long expectedVersion) {}
    record AuditContext(UUID actorId, String actorEmail, String requestId) {}
}