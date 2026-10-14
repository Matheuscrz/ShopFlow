package com.matheuscrz.identity.adapter.out.persistence;

import com.matheuscrz.identity.AbstractIntegrationTest;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.RefreshToken;
import com.matheuscrz.identity.domain.model.RefreshTokenStatus;
import com.matheuscrz.identity.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenPersistenceIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private RefreshTokenPersistenceAdapter adapter;

        @Autowired
        private SpringDataRefreshTokenJpaRepository repository;

        @Autowired
        private UserRepositoryPort userRepository;

        @Test
        @Transactional
        @DisplayName("Deve persistir, buscar por hash e revogar a família no PostgreSQL via Testcontainers")
        void shouldPersistAndRevokeTokensInFamily() {
                Instant now = Instant.now();
                UUID userId = UUID.randomUUID();

                // 1. Criar e persistir o usuário usando o método de domínio para satisfazer a
                // FK no banco
                User user = User.registerCustomer(
                                userId,
                                Email.of("teste.persist@shopflow.dev"),
                                "$argon2id$v=19$m=65536,t=3,p=1$fakeHashParaTeste",
                                "Usuário Teste",
                                now);
                userRepository.save(user);

                // 2. Instanciar e persistir os tokens vinculados ao ID do usuário persistido
                UUID familyId = UUID.randomUUID();
                RefreshToken token1 = RefreshToken.issue(
                                UUID.randomUUID(), userId, "hash_token_1", familyId, now, Duration.ofDays(7));
                RefreshToken token2 = RefreshToken.issue(
                                UUID.randomUUID(), userId, "hash_token_2", familyId, now, Duration.ofDays(7));

                adapter.save(token1);
                adapter.save(token2);

                // 3. Validações
                Optional<RefreshToken> found = adapter.findByTokenHash("hash_token_1");
                assertThat(found).isPresent();
                assertThat(found.get().familyId()).isEqualTo(familyId);
                assertThat(found.get().status()).isEqualTo(RefreshTokenStatus.ACTIVE);

                // Revoga toda a família
                adapter.revokeAllByFamilyId(familyId);

                var entities = repository.findAll();
                assertThat(entities)
                                .filteredOn(e -> e.getFamilyId().equals(familyId))
                                .allMatch(e -> e.getStatus() == RefreshTokenStatus.REVOKED);
        }
}