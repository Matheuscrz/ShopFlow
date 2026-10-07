package com.matheuscrz.identity.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

import com.matheuscrz.identity.domain.model.UserAddress;

@Entity
@Table(name = "user_addresses")
public class UserAddressJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column(nullable = false, length = 150)
    private String street;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(length = 150)
    private String complement;

    @Column(nullable = false, length = 100)
    private String neighborhood;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String country;

    protected UserAddressJpaEntity() {
    }

    UserAddressJpaEntity(UUID id, UserJpaEntity user, String zipCode,
            String street, String number, String complement,
            String neighborhood, String city, String state,
            String country, Instant createdAt) {
        super(id);
        this.user = user;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    UserJpaEntity getUser() {
        return user;
    }

    String getZipCode() {
        return zipCode;
    }

    String getStreet() {
        return street;
    }

    String getNumber() {
        return number;
    }

    String getComplement() {
        return complement;
    }

    String getNeighborhood() {
        return neighborhood;
    }

    String getCity() {
        return city;
    }

    String getState() {
        return state;
    }

    String getCountry() {
        return country;
    }

    void applyChanges(UserAddress address) {
        this.zipCode = address.zipCode();
        this.street = address.street();
        this.number = address.number();
        this.complement = address.complement();
        this.neighborhood = address.neighborhood();
        this.city = address.city();
        this.state = address.state();
        this.country = address.country();
    }
}