package com.matheuscrz.catalog.application.port.in;

import com.matheuscrz.catalog.domain.model.Category;

import java.util.UUID;

public interface ManageCategoryUseCase {

    Category createCategory(CreateCategoryCommand command);
    Category updateCategory(UUID id, UpdateCategoryCommand command);

    record CreateCategoryCommand(String name, String slug) {}
    record UpdateCategoryCommand(String name, String slug) {}
}