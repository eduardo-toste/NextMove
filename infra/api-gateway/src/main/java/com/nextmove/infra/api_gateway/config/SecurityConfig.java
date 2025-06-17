package com.nextmove.infra.api_gateway.config;

import com.nextmove.infra.api_gateway.exception.AuthenticationErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${api.security.token.secret}")
    private String jwtSecret;

    private final AuthenticationErrorHandler errorHandler;

    public SecurityConfig(AuthenticationErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth-service/auth/**").permitAll()
                        .pathMatchers("/auth-service/auth/users/**").hasAnyRole("SERVICE")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(errorHandler)
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}