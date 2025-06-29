package com.nextmove.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String username,

        @NotBlank
        @Size(min = 6)
        String password) {
}
