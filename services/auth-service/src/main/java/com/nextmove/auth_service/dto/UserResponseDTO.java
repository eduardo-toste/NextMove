package com.nextmove.auth_service.dto;

import com.nextmove.auth_service.model.User;

public record UserResponseDTO(

        String name,
        String username

) {

    public UserResponseDTO(User user){
        this(user.getName(), user.getUsername());
    }

}