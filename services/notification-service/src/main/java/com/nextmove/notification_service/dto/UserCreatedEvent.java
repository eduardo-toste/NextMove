package com.nextmove.notification_service.dto;

public record UserCreatedEvent(
        String name,
        String email
) {

}