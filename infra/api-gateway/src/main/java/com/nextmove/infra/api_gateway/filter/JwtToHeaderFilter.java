package com.nextmove.infra.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtToHeaderFilter implements GlobalFilter, Ordered {

    @Value("${api.security.token.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                    .withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

            return decoder.decode(token).flatMap(jwt -> {
                String username = jwt.getSubject();
                String userId = jwt.getClaimAsString("id");

                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Id", userId)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            });
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}