package com.matheuscrz.identity.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class UserAddress {

    private final UUID id;
    private final UUID userId;
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
    private final Instant createdAt;

    private UserAddress(UUID id, UUID userId, String zipCode,
            String street, String number, String complement,
            String neighborhood, String city, String state,
            String country, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.zipCode = require(zipCode, "O CEP é obrigatório.");
        this.street = require(street, "A rua é obrigatória.");
        this.number = require(number, "O número é obrigatório.");
        this.complement = complement;
        this.neighborhood = require(neighborhood, "O bairro é obrigatório.");
        this.city = require(city, "A cidade é obrigatória.");
        this.state = require(state, "O estado é obrigatório.");
        this.country = require(country, "O país é obrigatório.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static UserAddress create(UUID id, UUID userId, String zipCode,
            String street, String number,
            String complement, String neighborhood,
            String city, String state, String country,
            Instant now) {
        return new UserAddress(id, userId, zipCode, street, number, complement,
                neighborhood, city, state, country, now);
    }

    public static UserAddress restore(UUID id, UUID userId, String zipCode,
            String street, String number,
            String complement, String neighborhood,
            String city, String state, String country,
            Instant createdAt) {
        return new UserAddress(id, userId, zipCode, street, number, complement,
                neighborhood, city, state, country, createdAt);
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.strip();
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String zipCode() {
        return zipCode;
    }

    public String street() {
        return street;
    }

    public String number() {
        return number;
    }

    public String complement() {
        return complement;
    }

    public String neighborhood() {
        return neighborhood;
    }

    public String city() {
        return city;
    }

    public String state() {
        return state;
    }

    public String country() {
        return country;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void update(String zipCode, String street, String number,
            String complement, String neighborhood,
            String city, String state, String country) {
        this.zipCode = require(zipCode, "O CEP é obrigatório.");
        this.street = require(street, "A rua é obrigatória.");
        this.number = require(number, "O número é obrigatório.");
        this.complement = complement;
        this.neighborhood = require(neighborhood, "O bairro é obrigatório.");
        this.city = require(city, "A cidade é obrigatória.");
        this.state = require(state, "O estado é obrigatório.");
        this.country = require(country, "O país é obrigatório.");
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof UserAddress other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}