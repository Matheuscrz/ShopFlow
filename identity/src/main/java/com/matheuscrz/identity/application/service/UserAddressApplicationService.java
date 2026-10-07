package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ManageUserAddressUseCase;
import com.matheuscrz.identity.application.port.out.UserAddressRepositoryPort;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.exception.InvalidUserDataException;
import com.matheuscrz.identity.domain.model.UserAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class UserAddressApplicationService implements ManageUserAddressUseCase {

    private final UserAddressRepositoryPort addressRepository;
    private final UserRepositoryPort userRepository;
    private final UserNotificationEventPort notificationEventPort;
    private final Clock clock;

    public UserAddressApplicationService(
            UserAddressRepositoryPort addressRepository,
            UserRepositoryPort userRepository,
            UserNotificationEventPort notificationEventPort,
            Clock clock) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.notificationEventPort = notificationEventPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public UserAddress addAddress(AddAddressCommand cmd) {
        userRepository.findById(cmd.userId())
                .orElseThrow(() -> new InvalidUserDataException("Usuário não encontrado."));

        UserAddress address = UserAddress.create(
                UUID.randomUUID(),
                cmd.userId(),
                UserInputNormalizer.zipCode(cmd.zipCode()),
                UserInputNormalizer.text(cmd.street()),
                UserInputNormalizer.text(cmd.number()),
                UserInputNormalizer.text(cmd.complement()),
                UserInputNormalizer.text(cmd.neighborhood()),
                UserInputNormalizer.text(cmd.city()),
                UserInputNormalizer.text(cmd.state()),
                UserInputNormalizer.text(cmd.country()),
                clock.instant());

        UserAddress savedAddress = addressRepository.save(address);

        notificationEventPort.publishAddressChanged(
                UserAddressChangedEvent.of(
                        cmd.userId(),
                        savedAddress.id(),
                        "CREATED",
                        clock.instant()));

        return savedAddress;
    }

    @Override
    @Transactional
    public UserAddress updateAddress(UpdateAddressCommand cmd) {
        UserAddress address = addressRepository.findByIdAndUserId(
                cmd.addressId(),
                cmd.userId())
                .orElseThrow(() -> new InvalidUserDataException("Endereço não encontrado."));

        address.update(
                UserInputNormalizer.zipCode(cmd.zipCode()),
                UserInputNormalizer.text(cmd.street()),
                UserInputNormalizer.text(cmd.number()),
                UserInputNormalizer.text(cmd.complement()),
                UserInputNormalizer.text(cmd.neighborhood()),
                UserInputNormalizer.text(cmd.city()),
                UserInputNormalizer.text(cmd.state()),
                UserInputNormalizer.text(cmd.country()));

        UserAddress savedAddress = addressRepository.save(address);

        notificationEventPort.publishAddressChanged(
                UserAddressChangedEvent.of(
                        cmd.userId(),
                        savedAddress.id(),
                        "UPDATED",
                        clock.instant()));

        return savedAddress;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddress> listAddresses(UUID userId) {
        return addressRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void removeAddress(UUID id, UUID userId) {
        addressRepository.deleteByIdAndUserId(id, userId);

        notificationEventPort.publishAddressChanged(
                UserAddressChangedEvent.of(
                        userId,
                        id,
                        "REMOVED",
                        clock.instant()));
    }
}