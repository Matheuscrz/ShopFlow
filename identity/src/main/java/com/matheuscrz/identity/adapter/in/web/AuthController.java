package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.adapter.in.web.dto.AuthResponse;
import com.matheuscrz.identity.adapter.in.web.dto.LoginRequest;
import com.matheuscrz.identity.adapter.in.web.dto.RefreshTokenRequest;
import com.matheuscrz.identity.application.port.in.AuthenticateUserUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário com e-mail e senha, retornando tokens de acesso e refresh.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var command = new AuthenticateUserUseCase.LoginCommand(request.email(), request.password());
        var tokens = authenticateUserUseCase.login(command);
        return ResponseEntity.ok(AuthResponse.fromDomain(tokens));
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualizar tokens", description = "Atualiza os tokens de acesso e refresh usando um token de refresh válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens atualizados com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Token de refresh inválido ou expirado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var command = new AuthenticateUserUseCase.RefreshCommand(request.refreshToken());
        var tokens = authenticateUserUseCase.refreshToken(command);
        return ResponseEntity.ok(AuthResponse.fromDomain(tokens));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout do usuário", description = "Realiza o logout do usuário invalidando o token de refresh.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Token de refresh inválido ou expirado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        var command = new AuthenticateUserUseCase.RefreshCommand(request.refreshToken());
        authenticateUserUseCase.logout(command);
        return ResponseEntity.noContent().build();
    }
}