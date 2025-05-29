package com.nextmove.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "User")
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String username;
    private String password;

}
