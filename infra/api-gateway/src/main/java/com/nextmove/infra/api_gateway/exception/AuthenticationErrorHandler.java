package com.nextmove.infra.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextmove.infra.api_gateway.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationErrorHandler implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest().getPath().value();
        ErrorResponseDTO error = ErrorResponseDTO.of(status.value(), status.name(), message, path);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(error);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, org.springframework.security.core.AuthenticationException ex) {
        return writeResponse(exchange, HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
    }
}