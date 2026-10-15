package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.domain.model.Cpf;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;

public final class UserPersistenceMapper {
    private UserPersistenceMapper() {
    }

    static User toDomain(UserJpaEntity entity, Email email, Cpf cpf, String phone) {
        return User.restore(
                entity.getId(),
                email,
                entity.getPasswordHash(),
                entity.getName(),
                cpf,
                phone,
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    static UserJpaEntity toEntity(
            User user,
            String emailCiphertext,
            String emailLookupHash,
            String cpfCiphertext,
            String cpfLookupHash,
            String phoneCiphertext,
            String phoneLookupHash) {
        return new UserJpaEntity(
                user.id(),
                emailCiphertext,
                emailLookupHash,
                user.passwordHash(),
                user.name(),
                user.role(),
                user.status(),
                cpfCiphertext,
                cpfLookupHash,
                phoneCiphertext,
                phoneLookupHash);
    }
}