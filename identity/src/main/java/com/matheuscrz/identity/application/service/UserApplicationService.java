package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ManageUserUseCase;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;
import com.matheuscrz.identity.domain.exception.EmailAlreadyExistsException;
import com.matheuscrz.identity.domain.exception.InvalidCredentialsException;
import com.matheuscrz.identity.domain.exception.InvalidUserDataException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class UserApplicationService implements ManageUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordSecurityPort passwordSecurity;
    private final UserNotificationEventPort notificationEventPort;
    private final Clock clock;

    public UserApplicationService(
            UserRepositoryPort userRepository,
            PasswordSecurityPort passwordSecurity,
            UserNotificationEventPort notificationEventPort,
            Clock clock) {
        this.userRepository = userRepository;
        this.passwordSecurity = passwordSecurity;
        this.notificationEventPort = notificationEventPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public User register(RegisterUserCommand command) {
        String normalizedEmail = UserInputNormalizer.email(command.email());
        String normalizedName = UserInputNormalizer.name(command.name());
        String sanitizedPassword = UserInputNormalizer.sanitizePassword(command.rawPassword());

        Email email = Email.of(normalizedEmail);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }

        String passwordHash = passwordSecurity.encode(sanitizedPassword);
        User newUser = User.registerCustomer(
                UUID.randomUUID(),
                email,
                passwordHash,
                normalizedName,
                clock.instant());
        User savedUser = userRepository.save(newUser);

        notificationEventPort.publishUserRegistered(
                UserRegisteredEvent.of(
                        savedUser.id(),
                        savedUser.email().value(),
                        savedUser.name()));

        return savedUser;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        User user = findUserByIdOrThrow(command.userId());
        String currentPassword = UserInputNormalizer.sanitizePassword(command.currentPassword());
        String newPassword = UserInputNormalizer.sanitizePassword(command.newPassword());

        if (!passwordSecurity.matches(currentPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String newPasswordHash = passwordSecurity.encode(newPassword);
        user.changePasswordHash(newPasswordHash);
        userRepository.save(user);

        notificationEventPort.publishPasswordChanged(
                PasswordChangedEvent.of(
                        user.id(),
                        user.email().value(),
                        clock.instant()));
    }

    @Override
    @Transactional
    public User update(UpdateUserCommand command) {
        User user = findUserByIdOrThrow(command.userId());

        if (command.name() != null) {
            user.rename(UserInputNormalizer.name(command.name()));
        }

        User savedUser = userRepository.save(user);

        notificationEventPort.publishUserUpdated(
                UserUpdatedEvent.of(
                        savedUser.id(),
                        savedUser.email().value(),
                        savedUser.name(),
                        clock.instant()));

        return savedUser;
    }

    @Override
    @Transactional
    public void disable(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        user.disable();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void enable(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        user.enable();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(UUID userId) {
        return findUserByIdOrThrow(userId);
    }

    private User findUserByIdOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserDataException("Usuário não encontrado."));
    }
}