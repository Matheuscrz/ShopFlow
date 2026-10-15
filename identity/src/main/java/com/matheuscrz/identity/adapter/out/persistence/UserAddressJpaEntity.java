package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.adapter.out.persistence.converter.SensitiveDataConverter;
import com.matheuscrz.identity.domain.model.UserAddress;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
public class UserAddressJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "zip_code_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String zipCode;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "street_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String street;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "number_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String number;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "complement_ciphertext", columnDefinition = "TEXT")
    private String complement;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "neighborhood_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String neighborhood;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "city_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String city;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "state_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String state;

    @Convert(converter = SensitiveDataConverter.class)
    @Column(name = "country_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String country;

    protected UserAddressJpaEntity() {
    }

    public UserAddressJpaEntity(UUID id, UserJpaEntity user, String zipCode,
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

    public UserJpaEntity getUser() {
        return user;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public void applyChanges(UserAddress address) {
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