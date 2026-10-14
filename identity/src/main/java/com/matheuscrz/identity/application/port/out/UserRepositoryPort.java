package com.matheuscrz.identity.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.matheuscrz.identity.domain.model.Cpf;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Optional<User> findByCpf(Cpf cpf);

    boolean existsByCpf(Cpf cpf);

    Optional<User> findByCpfLookupHash(String lookupHash);

    boolean existsByCpfLookupHash(String lookupHash);
}
