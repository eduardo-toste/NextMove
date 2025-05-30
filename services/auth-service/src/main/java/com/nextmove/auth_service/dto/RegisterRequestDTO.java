package com.nextmove.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String username,

        @NotBlank
        @Min(6)
        String password) {
}
