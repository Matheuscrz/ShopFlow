package com.matheuscrz.identity.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.matheuscrz.identity.domain.model.UserAddress;

public interface UserAddressRepositoryPort {
    UserAddress save(UserAddress userAddress);
    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);
    List<UserAddress> findAllByUserId(UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
