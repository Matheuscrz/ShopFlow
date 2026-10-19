package com.matheuscrz.catalog.domain.model;

import com.matheuscrz.catalog.domain.exception.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Category {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");

    private final UUID id;
    private String name;
    private String slug;
    private final Instant createdAt;

    public Category(UUID id, String name, String slug, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Category ID cannot be null");
        this.setName(name);
        this.setSlug(slug);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Category create(String name, String slug) {
        return new Category(UUID.randomUUID(), name, slug, Instant.now());
    }

    public void update(String name, String slug) {
        setName(name);
        setSlug(slug);
    }

    public void setName(String name) {
        if (name == null || name.isBlank() || name.trim().length() < 2 || name.trim().length() > 100) {
            throw new DomainException("Category name must be between 2 and 100 characters");
        }
        this.name = name.trim();
    }

    public void setSlug(String slug) {
        if (slug == null || slug.isBlank() || !SLUG_PATTERN.matcher(slug.trim().toLowerCase()).matches()) {
            throw new DomainException("Invalid category slug format. Must be lowercase alphanumeric with hyphens");
        }
        this.slug = slug.trim().toLowerCase();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}