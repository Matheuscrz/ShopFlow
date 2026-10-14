package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.adapter.in.web.dto.ChangePasswordRequest;
import com.matheuscrz.identity.adapter.in.web.dto.RegisterUserRequest;
import com.matheuscrz.identity.adapter.in.web.dto.UpdateUserRequest;
import com.matheuscrz.identity.adapter.in.web.dto.UserResponse;
import com.matheuscrz.identity.application.port.in.ManageUserUseCase;
import com.matheuscrz.identity.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    private final ManageUserUseCase manageUserUseCase;

    public UserController(ManageUserUseCase manageUserUseCase) {
        this.manageUserUseCase = manageUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário com e-mail, senha e nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "E-mail já registrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var command = new ManageUserUseCase.RegisterUserCommand(
                request.email(),
                request.password(),
                request.name());
        User user = manageUserUseCase.register(command);
        URI location = URI.create("/api/users/" + user.id());
        return ResponseEntity.created(location).body(UserResponse.fromDomain(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obter usuário por ID", description = "Retorna os detalhes de um usuário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = manageUserUseCase.getById(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualizar usuário", description = "Atualiza os detalhes de um usuário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        var command = new ManageUserUseCase.UpdateUserCommand(id, request.name());
        User user = manageUserUseCase.update(command);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Alterar senha do usuário", description = "Altera a senha de um usuário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> changePassword(@PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        var command = new ManageUserUseCase.ChangePasswordCommand(id, request.currentPassword(), request.newPassword());
        manageUserUseCase.changePassword(command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário específico pelo seu ID. Restrito a ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> disable(@PathVariable UUID id) {
        manageUserUseCase.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário específico pelo seu ID. Restrito a ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> enable(@PathVariable UUID id) {
        manageUserUseCase.enable(id);
        return ResponseEntity.noContent().build();
    }
}