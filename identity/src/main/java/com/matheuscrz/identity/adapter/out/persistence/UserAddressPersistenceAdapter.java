package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.application.port.out.UserAddressRepositoryPort;
import com.matheuscrz.identity.domain.model.UserAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserAddressPersistenceAdapter implements UserAddressRepositoryPort {

    private final SpringDataUserAddressJpaRepository addressRepository;
    private final SpringDataUserJpaRepository userRepository;

    public UserAddressPersistenceAdapter(
            SpringDataUserAddressJpaRepository addressRepository,
            SpringDataUserJpaRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserAddress save(UserAddress address) {
        UserAddressJpaEntity entity = addressRepository.findById(address.id())
                .map(existing -> {
                    existing.applyChanges(address);
                    return existing;
                })
                .orElseGet(() -> {
                    UserJpaEntity userRef = userRepository.getReferenceById(address.userId());
                    return UserAddressPersistenceMapper.toEntity(address, userRef);
                });

        return UserAddressPersistenceMapper.toDomain(addressRepository.save(entity));
    }

    @Override
    public Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId) {
        return addressRepository.findByIdAndUserId(id, userId)
                .map(UserAddressPersistenceMapper::toDomain);
    }

    @Override
    public List<UserAddress> findAllByUserId(UUID userId) {
        return addressRepository.findAllByUserId(userId).stream()
                .map(UserAddressPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByIdAndUserId(UUID id, UUID userId) {
        addressRepository.deleteByIdAndUserId(id, userId);
    }
}