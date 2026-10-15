package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.AbstractIntegrationTest;
import com.matheuscrz.identity.adapter.in.web.dto.RegisterUserRequest;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private UserRepositoryPort userRepository;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Autowired
        private SensitiveDataSecurityPort sensitiveDataSecurity;

        @BeforeEach
        void cleanUp() {
                jdbcTemplate.execute("DELETE FROM users");
        }

        @Test
        @DisplayName("Deve cadastrar usuário com sucesso no PostgreSQL e verificar persistência criptografada")
        void shouldRegisterUserAndPersistEncryptedCpfInPostgres() {
                RegisterUserRequest request = new RegisterUserRequest(
                                "usuario.real@shopflow.dev",
                                "SenhaForte#2026",
                                "Matheus Dev",
                                "52998224725",
                                "11999998888");

                ResponseEntity<Map> response = restTemplate.postForEntity(
                                "/api/users/register",
                                request,
                                Map.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("email")).isEqualTo("usuario.real@shopflow.dev");

                // 1. Validação via Port oficial (decifrado em memória)
                Optional<User> userOpt = userRepository.findByEmail(Email.of("usuario.real@shopflow.dev"));
                assertThat(userOpt).isPresent();
                assertThat(userOpt.get().name()).isEqualTo("Matheus Dev");
                assertThat(userOpt.get().cpf().value()).isEqualTo("52998224725");

                // 2. Validação direta no PostgreSQL para garantir que o CPF NÃO está em texto
                // puro
                String emailLookupHash = sensitiveDataSecurity.generateLookupHash("usuario.real@shopflow.dev");

                Map<String, Object> rawDbRow = jdbcTemplate.queryForMap(
                                """
                                                SELECT email_ciphertext, cpf_ciphertext, cpf_lookup_hash
                                                FROM users
                                                WHERE email_lookup_hash = ?
                                                """,
                                emailLookupHash);

                String emailCiphertext = (String) rawDbRow.get("email_ciphertext");
                String ciphertext = (String) rawDbRow.get("cpf_ciphertext");
                String lookupHash = (String) rawDbRow.get("cpf_lookup_hash");

                assertThat(emailCiphertext).isNotBlank();
                assertThat(ciphertext).isNotBlank();
                assertThat(ciphertext).doesNotContain("52998224725");
                assertThat(lookupHash).hasSize(64);
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict com opção de recuperação ao tentar cadastrar CPF existente")
        void shouldReturn409WithRecoveryPromptWhenCpfAlreadyExists() {
                // Primeiro cadastro
                RegisterUserRequest firstRequest = new RegisterUserRequest(
                                "titular@shopflow.dev",
                                "SenhaForte#2026",
                                "Titular Conta",
                                "52998224725",
                                "11999998888");
                restTemplate.postForEntity("/api/users/register", firstRequest, Map.class);

                // Segundo cadastro com outro e-mail, mas MESMO CPF
                RegisterUserRequest duplicateCpfRequest = new RegisterUserRequest(
                                "outro.email@shopflow.dev",
                                "SenhaForte#2026",
                                "Tentativa Fraude",
                                "529.982.247-25",
                                "11988887777");

                ResponseEntity<Map> response = restTemplate.postForEntity(
                                "/api/users/register",
                                duplicateCpfRequest,
                                Map.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                Map<?, ?> body = response.getBody();
                assertThat(body).isNotNull();
                assertThat(body.get("type")).isEqualTo("https://shopflow.dev/errors/cpf-already-exists");
                assertThat(body.get("maskedEmail")).isEqualTo("t***@shopflow.dev");
                assertThat(body.get("canRecoverPassword")).isEqualTo(true);
                assertThat(body.get("message").toString()).contains("t***@shopflow.dev");
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict ao tentar cadastrar e-mail já existente com outro CPF")
        void shouldReturn409WhenEmailAlreadyExists() {
                // Primeiro cadastro
                RegisterUserRequest firstRequest = new RegisterUserRequest(
                                "duplicado@shopflow.dev",
                                "SenhaForte#2026",
                                "Primeiro",
                                "52998224725",
                                "11999998888");
                restTemplate.postForEntity("/api/users/register", firstRequest, Map.class);

                // Segundo cadastro: mesmo e-mail, mas com outro CPF matematicamente válido
                RegisterUserRequest duplicateEmailRequest = new RegisterUserRequest(
                                "duplicado@shopflow.dev",
                                "OutraSenha#2026",
                                "Segundo",
                                "11144477735",
                                "11988887777");

                ResponseEntity<Map> response = restTemplate.postForEntity(
                                "/api/users/register",
                                duplicateEmailRequest,
                                Map.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                Map<?, ?> body = response.getBody();
                assertThat(body).isNotNull();
                assertThat(body.get("type")).isEqualTo("https://shopflow.dev/errors/email-already-exists");
        }
}