package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.adapter.in.web.dto.AuthResponse;
import com.matheuscrz.identity.adapter.in.web.dto.LoginRequest;
import com.matheuscrz.identity.application.port.in.AuthenticateUserUseCase;
import com.matheuscrz.identity.config.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gestão de sessão de utilizadores")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7);

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtProperties jwtProperties;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, JwtProperties jwtProperties) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Autenticar utilizador", description = "Autentica via email/password, emitindo Access Token no body e Refresh Token via Cookie HttpOnly.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var command = new AuthenticateUserUseCase.LoginCommand(request.email(), request.password());
        var tokens = authenticateUserUseCase.login(command);

        ResponseCookie cookie = createRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_MAX_AGE);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.fromDomain(tokens));
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualizar tokens", description = "Renova o Access Token e rotaciona o Refresh Token através do Cookie HttpOnly recebido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou reutilizado", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var command = new AuthenticateUserUseCase.RefreshCommand(refreshToken);
        var tokens = authenticateUserUseCase.refreshToken(command);

        ResponseCookie cookie = createRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_MAX_AGE);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.fromDomain(tokens));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout de sessão", description = "Revoga o Refresh Token no servidor e expira o cookie no cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sessão finalizada com sucesso")
    })
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticateUserUseCase.logout(new AuthenticateUserUseCase.RefreshCommand(refreshToken));
        }

        ResponseCookie cleanCookie = createRefreshTokenCookie("", Duration.ZERO);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
    }
}