package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.service.UserInputNormalizer;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres") String name,

        @Size(min = 8, max = 200, message = "A senha deve ter entre 8 e 200 caracteres") String password) {

    public UpdateUserRequest {
        name = UserInputNormalizer.name(name);
    }
}