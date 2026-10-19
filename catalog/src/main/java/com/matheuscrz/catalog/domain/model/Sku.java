package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

public record Sku(String value) {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9_-]{3,50}$");

    public Sku {
        Objects.requireNonNull(value, "SKU cannot be null");
        String normalized = value.trim().toUpperCase();
        if (!SKU_PATTERN.matcher(normalized).matches()) {
            throw new DomainException("Invalid SKU format. Must match [A-Z0-9_-]{3,50}: " + value);
        }
        value = normalized;
    }
}