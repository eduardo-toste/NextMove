package com.nextmove.transaction_service.config;

import com.nextmove.transaction_service.util.GetEnv;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    private final String serviceToken = GetEnv.get("FIXED_SERVICE_TOKEN");

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + serviceToken);
    }
}