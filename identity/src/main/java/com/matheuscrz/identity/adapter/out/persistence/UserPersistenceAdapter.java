package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserJpaRepository repository;

    public UserPersistenceAdapter(SpringDataUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = repository.findById(user.id())
                .map(existing -> {
                    existing.applyChanges(user);
                    return existing;
                })
                .orElseGet(() -> UserPersistenceMapper.toEntity(user));

        return UserPersistenceMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}