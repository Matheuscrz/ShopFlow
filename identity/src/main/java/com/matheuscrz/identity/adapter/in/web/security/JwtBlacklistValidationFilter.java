package com.matheuscrz.identity.adapter.in.web.security;

import com.matheuscrz.identity.application.port.out.TokenBlacklistPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtBlacklistValidationFilter extends OncePerRequestFilter {

    private final TokenBlacklistPort tokenBlacklist;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String jti = jwt.getId();
            String sub = jwt.getSubject();
            Instant issuedAt = jwt.getIssuedAt();

            boolean isRevoked = false;

            if (jti != null && tokenBlacklist.isBlacklisted(jti)) {
                isRevoked = true;
            } else if (sub != null && issuedAt != null) {
                try {
                    UUID userId = UUID.fromString(sub);
                    if (tokenBlacklist.isUserRevoked(userId, issuedAt)) {
                        isRevoked = true;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (isRevoked) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.getWriter().write("""
                    {
                      "type": "https://shopflow.dev/errors/invalid-token",
                      "title": "Token Revogado",
                      "status": 401,
                      "detail": "O token apresentado foi revogado."
                    }
                    """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}