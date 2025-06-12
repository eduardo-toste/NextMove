package com.nextmove.notification_service.dto;

public record UserCreatedEvent(
        String nome,
        String email
) {

}