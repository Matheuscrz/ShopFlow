package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("Deve normalizar o e-mail em minúsculas e sem espaços laterais")
    void shouldNormalizeValidEmail() {
        Email email = new Email("  User.Test@Shopflow.Dev  ");
        assertThat(email.value()).isEqualTo("user.test@shopflow.dev");
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   ", "plainaddress", "#@%^%#$@#$@#.com", "@example.com", "user@.com" })
    @DisplayName("Deve rejeitar formatos de e-mail inválidos")
    void shouldRejectInvalidEmail(String rawEmail) {
        assertThatThrownBy(() -> new Email(rawEmail))
                .isInstanceOf(InvalidEmailException.class);
    }
}