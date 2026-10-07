package com.matheuscrz.identity.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        this.user = User.restore(
                UUID.randomUUID(),
                Email.of("dev@shopflow.dev"),
                "$argon2id$v=19$m=65536,t=3,p=1$fakeHash",
                "Matheus Cruz",
                Role.CUSTOMER,
                UserStatus.ACTIVE,
                Instant.now());
    }

    @Test
    @DisplayName("Deve desativar o utilizador e alterar o status para INACTIVE")
    void shouldDisableUser() {
        user.disable();
        assertThat(user.status()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.canAuthenticate()).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar o hash de senha com sucesso")
    void shouldUpdatePasswordHash() {
        String newHash = "$argon2id$v=19$m=65536,t=3,p=1$newFakeHash";
        user.changePasswordHash(newHash);
        assertThat(user.passwordHash()).isEqualTo(newHash);
    }
}