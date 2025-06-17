package com.nextmove.infra.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                .route("transaction-service", r -> r.path("/transactions/**")
                        .uri("lb://transaction-service"))
                .build();
    }
}