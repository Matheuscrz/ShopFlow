package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.adapter.in.web.dto.CreateUserAddressRequest;
import com.matheuscrz.identity.adapter.in.web.dto.UpdateUserAddressRequest;
import com.matheuscrz.identity.adapter.in.web.dto.UserAddressResponse;
import com.matheuscrz.identity.application.port.in.ManageUserAddressUseCase;
import com.matheuscrz.identity.domain.model.UserAddress;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@Tag(name = "Endereços de Usuário", description = "Endpoints para gerenciamento de endereços de usuários")
public class UserAddressController {

    private final ManageUserAddressUseCase manageUserAddressUseCase;

    public UserAddressController(ManageUserAddressUseCase manageUserAddressUseCase) {
        this.manageUserAddressUseCase = manageUserAddressUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar endereço", description = "Adiciona um novo endereço para um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço adicionado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserAddressResponse> addAddress(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateUserAddressRequest request) {
        var command = new ManageUserAddressUseCase.AddAddressCommand(
                userId,
                request.zipCode(),
                request.street(),
                request.number(),
                request.complement(),
                request.neighborhood(),
                request.city(),
                request.state(),
                request.country());
        UserAddress address = manageUserAddressUseCase.addAddress(command);
        URI location = URI.create("/api/v1/users/" + userId + "/addresses/" + address.id());
        return ResponseEntity.created(location).body(UserAddressResponse.fromDomain(address));
    }

    @PutMapping("/{addressId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualizar endereço", description = "Atualiza os detalhes de um endereço específico de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Endereço ou usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateUserAddressRequest request) {
        var command = new ManageUserAddressUseCase.UpdateAddressCommand(
                addressId,
                userId,
                request.zipCode(),
                request.street(),
                request.number(),
                request.complement(),
                request.neighborhood(),
                request.city(),
                request.state(),
                request.country());
        UserAddress address = manageUserAddressUseCase.updateAddress(command);
        return ResponseEntity.ok(UserAddressResponse.fromDomain(address));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar endereços", description = "Lista todos os endereços de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereços listados com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<List<UserAddressResponse>> listAddresses(@PathVariable UUID userId) {
        List<UserAddressResponse> list = manageUserAddressUseCase.listAddresses(userId).stream()
                .map(UserAddressResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover endereço", description = "Remove um endereço específico de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço ou usuário não encontrado", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<Void> removeAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        manageUserAddressUseCase.removeAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}