package com.matheuscrz.identity.adapter.in.web.security;

import com.matheuscrz.identity.application.port.out.RateLimiterPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterPort rateLimiter;

    @Value("${app.security.rate-limit.login-max-attempts:5}")
    private int maxAttempts;

    @Value("${app.security.rate-limit.login-window-seconds:60}")
    private long windowSeconds;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if ("/api/auth/login".equalsIgnoreCase(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String clientIp = resolveClientIp(request);
            Duration window = Duration.ofSeconds(windowSeconds);

            boolean allowed = rateLimiter.tryAcquire("login", clientIp, maxAttempts, window);

            if (!allowed) {
                long waitSeconds = rateLimiter.getRemainingWaitSeconds("login", clientIp);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setHeader("Retry-After", String.valueOf(waitSeconds > 0 ? waitSeconds : windowSeconds));
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.getWriter().write("""
                    {
                      "type": "https://shopflow.dev/errors/too-many-requests",
                      "title": "Limite de Requisições Excedido",
                      "status": 429,
                      "detail": "Muitas tentativas de login a partir desta origem. Tente novamente mais tarde."
                    }
                    """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}