package com.matheuscrz.catalog.application.port.out;

import com.matheuscrz.catalog.domain.model.Product;
import com.matheuscrz.catalog.domain.model.Sku;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(UUID id);

    Optional<Product> findBySku(Sku sku);

    boolean existsBySku(Sku sku);

    List<Product> findAllByIdIn(Collection<UUID> ids);
}