package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    static User toDomain(UserJpaEntity entity) {
        return User.restore(
                entity.getId(),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getName(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id(),
                user.email().value(),
                user.passwordHash(),
                user.name(),
                user.role(),
                user.status());
    }
}
