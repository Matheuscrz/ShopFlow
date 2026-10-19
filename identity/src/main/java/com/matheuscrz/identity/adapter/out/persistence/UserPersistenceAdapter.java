package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.model.Cpf;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    private final SpringDataUserJpaRepository repository;
    private final SensitiveDataSecurityPort security;

    public UserPersistenceAdapter(
            SpringDataUserJpaRepository repository,
            SensitiveDataSecurityPort security) {
        this.repository = repository;
        this.security = security;
    }

    @Override
    public User save(User user) {
        String email = user.email().value();
        String emailCiphertext = security.encrypt(email);
        String emailLookupHash = security.generateLookupHash(email);

        String cpfCiphertext = user.cpf() == null
                ? null
                : security.encrypt(user.cpf().value());
        String cpfLookupHash = user.cpf() == null
                ? null
                : security.generateLookupHash(user.cpf().value());

        String phoneCiphertext = user.phone() == null
                ? null
                : security.encrypt(user.phone());
        String phoneLookupHash = user.phone() == null
                ? null
                : security.generateLookupHash(user.phone());

        UserJpaEntity entity = repository.findById(user.id())
                .map(existing -> {
                    existing.applyChanges(user);
                    existing.updateEmail(emailCiphertext, emailLookupHash);
                    existing.updateCpf(cpfCiphertext, cpfLookupHash);
                    existing.updatePhone(phoneCiphertext, phoneLookupHash);
                    return existing;
                })
                .orElseGet(() -> UserPersistenceMapper.toEntity(
                        user,
                        emailCiphertext,
                        emailLookupHash,
                        cpfCiphertext,
                        cpfLookupHash,
                        phoneCiphertext,
                        phoneLookupHash));

        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        String hash = security.generateLookupHash(email.value());
        return repository.findByEmailLookupHash(hash)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmailLookupHash(
                security.generateLookupHash(email.value()));
    }

    @Override
    public Optional<User> findByCpf(Cpf cpf) {
        return findByCpfLookupHash(
                security.generateLookupHash(cpf.value()));
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return existsByCpfLookupHash(
                security.generateLookupHash(cpf.value()));
    }

    @Override
    public Optional<User> findByCpfLookupHash(String lookupHash) {
        return repository.findByCpfLookupHash(lookupHash)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByCpfLookupHash(String lookupHash) {
        return repository.existsByCpfLookupHash(lookupHash);
    }

    private User toDomain(UserJpaEntity entity) {
        String decryptedEmail = security.decrypt(entity.getEmailCiphertext());
        Email email = Email.of(decryptedEmail);
        Cpf cpf = entity.getCpfCiphertext() == null
                ? null
                : new Cpf(security.decrypt(entity.getCpfCiphertext()));
        String phone = entity.getPhoneCiphertext() == null
                ? null
                : security.decrypt(entity.getPhoneCiphertext());

        return UserPersistenceMapper.toDomain(entity, email, cpf, phone);
    }
}