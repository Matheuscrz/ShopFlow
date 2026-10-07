package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.RegisterUserUseCase;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.exception.EmailAlreadyExistsException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class UserRegistrationApplicationService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordSecurityPort passwordSecurity;
    private final Clock clock;

    public UserRegistrationApplicationService(
            UserRepositoryPort userRepository,
            PasswordSecurityPort passwordSecurity,
            Clock clock) {
        this.userRepository = userRepository;
        this.passwordSecurity = passwordSecurity;
        this.clock = clock;
    }

    @Override
    @Transactional
    public User register(Command command) {
        String normalizedEmail = UserInputNormalizer.email(command.email());
        String normalizedName = UserInputNormalizer.name(command.name());

        Email email = Email.of(normalizedEmail);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }

        String passwordHash = passwordSecurity.encode(command.rawPassword());
        User newUser = User.registerCustomer(
                UUID.randomUUID(),
                email,
                passwordHash,
                normalizedName,
                clock.instant()
        );

        return userRepository.save(newUser);
    }
}