package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.application.port.out.CpfSecurityPort;
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
    private final CpfSecurityPort cpfSecurity;

    public UserPersistenceAdapter(SpringDataUserJpaRepository repository, CpfSecurityPort cpfSecurity) {
        this.repository = repository;
        this.cpfSecurity = cpfSecurity;
    }

    @Override
    public User save(User user) {
        String ciphertext = user.cpf() != null ? cpfSecurity.encrypt(user.cpf()) : null;
        String lookupHash = user.cpf() != null ? cpfSecurity.lookupHash(user.cpf()) : null;

        UserJpaEntity entity = repository.findById(user.id())
                .map(existing -> {
                    existing.applyChanges(user);
                    if (ciphertext != null) {
                        existing.updateCpf(ciphertext, lookupHash);
                    }
                    return existing;
                })
                .orElseGet(() -> UserPersistenceMapper.toEntity(user, ciphertext, lookupHash));

        return toDomainWithDecryptedCpf(repository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomainWithDecryptedCpf);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(this::toDomainWithDecryptedCpf);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public Optional<User> findByCpf(Cpf cpf) {
        return findByCpfLookupHash(cpfSecurity.lookupHash(cpf));
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return existsByCpfLookupHash(cpfSecurity.lookupHash(cpf));
    }

    @Override
    public Optional<User> findByCpfLookupHash(String lookupHash) {
        return repository.findByCpfLookupHash(lookupHash).map(this::toDomainWithDecryptedCpf);
    }

    @Override
    public boolean existsByCpfLookupHash(String lookupHash) {
        return repository.existsByCpfLookupHash(lookupHash);
    }

    private User toDomainWithDecryptedCpf(UserJpaEntity entity) {
        Cpf cpf = null;
        if (entity.getCpfCiphertext() != null) {
            cpf = cpfSecurity.decrypt(entity.getCpfCiphertext());
        }
        return UserPersistenceMapper.toDomain(entity, cpf);
    }
}