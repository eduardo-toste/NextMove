package com.nextmove.auth_service.controller;

import com.nextmove.auth_service.dto.AuthRequestDTO;
import com.nextmove.auth_service.dto.AuthResponseDTO;
import com.nextmove.auth_service.dto.RegisterRequestDTO;
import com.nextmove.auth_service.dto.UserDTO;
import com.nextmove.auth_service.service.AuthenticationService;
import com.nextmove.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO request) {
        AuthResponseDTO response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO request){
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UserDTO user = authenticationService.validateTokenAndGetUser(token);

        return ResponseEntity.ok(user);
    }

}
