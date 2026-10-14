package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.service.UserInputNormalizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A palavra-passe é obrigatória")
        String password
) {
    public LoginRequest {
        email = UserInputNormalizer.email(email);
    }
}