package com.matheuscrz.identity.application.port.in;

import java.util.List;
import java.util.UUID;

import com.matheuscrz.identity.domain.model.UserAddress;

public interface ManageUserAddressUseCase {
    record AddAddressCommand(
            UUID userId,
            String zipCode,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String country) {
    }

    record UpdateAddressCommand(
            UUID userId,
            UUID addressId,
            String zipCode,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String country) {
    }

    UserAddress addAddress(AddAddressCommand command);

    UserAddress updateAddress(UpdateAddressCommand command);

    List<UserAddress> listAddresses(UUID userId);

    void removeAddress(UUID id, UUID userId);
}
