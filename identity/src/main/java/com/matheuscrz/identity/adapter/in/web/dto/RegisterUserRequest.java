package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.service.UserInputNormalizer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") @Size(max = 254, message = "Email deve ter no máximo 254 caracteres") String email,

        @NotBlank(message = "Senha é obrigatória") @Size(min = 8, max = 200, message = "A senha deve ter entre 8 e 200 caracteres") String password,

        @NotBlank(message = "Nome é obrigatório") @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres") String name) {

    public RegisterUserRequest {
        email = UserInputNormalizer.email(email);
        name = UserInputNormalizer.name(name);
    }
}