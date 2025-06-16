package com.nextmove.transaction_service.client;

import com.nextmove.transaction_service.config.FeignClientConfig;
import com.nextmove.transaction_service.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "authClient",
        url = "${auth.service.url}",
        configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserResponseDTO getUserById(@PathVariable("id") UUID id);
}