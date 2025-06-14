package com.nextmove.auth_service.dto;

public record UserCreatedEvent(
        String name,
        String email
) {

}
