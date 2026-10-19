package com.matheuscrz.catalog.application.port.out;

import com.matheuscrz.catalog.domain.model.ProductInventory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort {
    ProductInventory save(ProductInventory inventory);

    Optional<ProductInventory> findByProductId(UUID productId);

    List<ProductInventory> findAllByProductIdIn(Collection<UUID> productIds);

    /**
     * Execução atômica no banco:
     * UPDATE product_inventory SET available_stock = available_stock - :quantity
     * WHERE product_id = :productId AND available_stock >= :quantity
     * 
     * @return true se debitou com sucesso, false se não havia estoque suficiente
     */
    boolean decrementStockAtomic(UUID productId, int quantity);

    /**
     * Devolução atômica para compensação:
     * UPDATE product_inventory SET available_stock = available_stock + :quantity
     * WHERE product_id = :productId
     */
    void incrementStockAtomic(UUID productId, int quantity);
}