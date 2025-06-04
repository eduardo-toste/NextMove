package com.nextmove.auth_service.dto;

import com.nextmove.auth_service.model.User;

import java.util.UUID;

public record UserDTO(

        UUID id,
        String name,
        String username

) {

    public UserDTO(User user){
        this(user.getId(), user.getName(), user.getUsername());
    }

}