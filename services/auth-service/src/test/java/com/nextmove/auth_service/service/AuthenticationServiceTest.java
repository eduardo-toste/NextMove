package com.nextmove.auth_service.service;

import com.nextmove.auth_service.dto.AuthRequestDTO;
import com.nextmove.auth_service.dto.AuthResponseDTO;
import com.nextmove.auth_service.exception.ResourceNotFoundException;
import com.nextmove.auth_service.model.User;
import com.nextmove.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Test
    void shouldAuthenticate() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Eduardo", "eduardo@test.com", "encoded123");
        AuthRequestDTO request = new AuthRequestDTO(user.getUsername(), user.getPassword());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getUsername(), user.getId())).thenReturn("validToken");

        // Act
        AuthResponseDTO result = authenticationService.authenticate(request);

        // Assert
        assertNotNull(result);
        assertEquals("validToken", result.token());
    }

    @Test
    void shouldNotAuthenticate() {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO("eduardo@test.com", "encoded123");

        when(authenticationManager.authenticate(any())).thenReturn(any());
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());

        // Act e Assert
        assertThrows(ResourceNotFoundException.class, () ->
                authenticationService.authenticate(request)
        );
    }

}