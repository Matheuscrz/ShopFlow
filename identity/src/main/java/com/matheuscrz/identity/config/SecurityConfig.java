package com.matheuscrz.identity.config;

import com.matheuscrz.identity.adapter.in.web.security.JwtBlacklistValidationFilter;
import com.matheuscrz.identity.adapter.in.web.security.LoginRateLimitFilter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private static final String[] SWAGGER_WHITELIST = {
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
        };

        private static final String[] PUBLIC_WHITELIST = {
                        "/api/auth/**",
                        "/api/users/register",
                        "/actuator/health",
                        "/.well-known/jwks.json"
        };

        @Value("${app.cors.allowed-origins}")
        private String allowedOrigins;

        @Value("${app.cors.enabled:false}")
        private boolean corsEnabled;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        LoginRateLimitFilter rateLimitFilter,
                        JwtBlacklistValidationFilter blacklistFilter,
                        JwtDecoder jwtDecoder) throws Exception {

                if (corsEnabled) {
                        http.cors(Customizer.withDefaults());
                } else {
                        http.cors(AbstractHttpConfigurer::disable);
                }

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                                .requestMatchers(PUBLIC_WHITELIST).permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .decoder(jwtDecoder)
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter())))
                                // Adiciona o rate limiter na entrada do filtro de autenticação
                                .addFilterBefore(rateLimitFilter,
                                                org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
                                // Adiciona a checagem da blacklist do Redis logo após o token ser validado
                                .addFilterAfter(blacklistFilter,
                                                org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
                                .headers(headers -> headers
                                                .contentTypeOptions(Customizer.withDefaults())
                                                .frameOptions(frame -> frame.deny())
                                                .referrerPolicy(referrer -> referrer.policy(
                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE)));

                return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder(RsaKeyProvider rsaKeyProvider, JwtProperties jwtProperties) {
                DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
                JWSVerificationKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                                JWSAlgorithm.RS256,
                                new ImmutableJWKSet<>(rsaKeyProvider.getPublicJwkSet()));
                jwtProcessor.setJWSKeySelector(keySelector);

                NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
                OAuth2TokenValidator<Jwt> defaultValidator =
                                JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer());
                OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<>(
                                "aud",
                                aud -> aud instanceof List<?> values
                                                && values.contains(jwtProperties.getAudience()));
                decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                                defaultValidator, audienceValidator));
                return decoder;
        }

        @Bean
        Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
                grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
                jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                return jwtConverter;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new Argon2PasswordEncoder(
                                32, // salt length
                                64, // hash length
                                1, // parallelism
                                65536, // memory cost: 64 MiB
                                3 // iterations
                );
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                Arrays.stream(allowedOrigins.split(","))
                                                .map(String::trim)
                                                .filter(origin -> !origin.isBlank())
                                                .toList());

                configuration.setAllowedMethods(List.of(
                                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type",
                                "Accept",
                                "Origin",
                                "X-Requested-With",
                                "X-Correlation-Id"));

                configuration.setExposedHeaders(List.of("X-Correlation-Id"));

                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}