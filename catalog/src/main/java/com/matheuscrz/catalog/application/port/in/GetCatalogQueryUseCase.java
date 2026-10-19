package com.matheuscrz.catalog.application.port.in;

import com.matheuscrz.catalog.domain.model.Money;
import com.matheuscrz.catalog.domain.model.Sku;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetCatalogQueryUseCase {

    PagedResult<CatalogProductSummaryView> searchProducts(CatalogSearchQuery query);

    Optional<CatalogProductDetailView> getProductDetail(UUID id);

    List<CategoryView> listAllCategories();

    record CatalogSearchQuery(String categorySlug, String searchTerm, int page, int size, String sort) {
    }

    record CatalogProductSummaryView(UUID id, Sku sku, String name, Money price, String categoryName,
            int availableStock) {
    }

    record CatalogProductDetailView(UUID id, Sku sku, String name, String description, Money price, UUID categoryId,
            String categoryName, int availableStock) {
    }

    record CategoryView(UUID id, String name, String slug) {
    }

    record PagedResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    }
}