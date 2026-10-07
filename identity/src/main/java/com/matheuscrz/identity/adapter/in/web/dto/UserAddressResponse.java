package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.domain.model.UserAddress;
import java.time.Instant;
import java.util.UUID;

public record UserAddressResponse(
        UUID id,
        UUID userId,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String country,
        Instant createdAt
) {
    public static UserAddressResponse fromDomain(UserAddress address) {
        return new UserAddressResponse(
                address.id(),
                address.userId(),
                address.zipCode(),
                address.street(),
                address.number(),
                address.complement(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.country(),
                address.createdAt()
        );
    }
}