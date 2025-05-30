package com.nextmove.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(

        @NotBlank
        @Email
        String username,

        @NotBlank
        String password) {
}
