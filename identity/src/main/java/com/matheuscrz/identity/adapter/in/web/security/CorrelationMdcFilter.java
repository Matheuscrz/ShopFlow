package com.matheuscrz.identity.adapter.in.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationMdcFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_REQUEST_PATH = "requestPath";
    public static final String MDC_USER_ID = "userId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());

        MDC.put(MDC_CORRELATION_ID, correlationId);
        MDC.put(MDC_REQUEST_PATH, request.getMethod() + " " + request.getRequestURI());

        // Adiciona header na resposta para rastreabilidade ponta a ponta
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                MDC.put(MDC_USER_ID, auth.getName());
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove(MDC_REQUEST_PATH);
            MDC.remove(MDC_USER_ID);
        }
    }
}