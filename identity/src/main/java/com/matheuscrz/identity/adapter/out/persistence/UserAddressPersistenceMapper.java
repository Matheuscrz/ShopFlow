package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.UserAddress;

public final class UserAddressPersistenceMapper {

    private UserAddressPersistenceMapper() {
    }

    static UserAddress toDomain(UserAddressJpaEntity entity) {
        return UserAddress.restore(
                entity.getId(),
                entity.getUser().getId(),
                entity.getZipCode(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getCountry(),
                entity.getCreatedAt());
    }

    static UserAddressJpaEntity toEntity(UserAddress address, UserJpaEntity userEntity) {
        return new UserAddressJpaEntity(
                address.id(),
                userEntity,
                address.zipCode(),
                address.street(),
                address.number(),
                address.complement(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.country(),
                address.createdAt());
    }
}