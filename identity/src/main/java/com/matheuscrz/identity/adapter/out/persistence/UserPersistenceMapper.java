package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Cpf;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    static User toDomain(UserJpaEntity entity, Cpf cpf) {
        return User.restore(
                entity.getId(),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getName(),
                cpf,
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    static UserJpaEntity toEntity(User user, String ciphertext, String lookupHash) {
        return new UserJpaEntity(
                user.id(),
                user.email().value(),
                user.passwordHash(),
                user.name(),
                user.role(),
                user.status(),
                ciphertext,
                lookupHash
        );
    }
}