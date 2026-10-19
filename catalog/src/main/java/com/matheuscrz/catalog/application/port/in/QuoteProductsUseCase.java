package com.matheuscrz.catalog.application.port.in;

import com.matheuscrz.catalog.domain.model.Money;
import com.matheuscrz.catalog.domain.model.Sku;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface QuoteProductsUseCase {

    List<QuotedProductView> quote(Set<UUID> productIds);

    record QuotedProductView(
            UUID productId,
            Sku sku,
            String name,
            Money unitPrice,
            boolean active,
            int availableStock) {
    }
}