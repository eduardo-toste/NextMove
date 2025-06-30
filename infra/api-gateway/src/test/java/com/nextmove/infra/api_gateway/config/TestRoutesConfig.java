package com.nextmove.infra.api_gateway.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRoutesConfig {
    @Bean
    public RouteLocator testRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth-service/**")
                        .uri("http://localhost:8089"))
                .build();
    }
}
