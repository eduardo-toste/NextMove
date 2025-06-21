package com.nextmove.auth_service.service;

import com.nextmove.auth_service.dto.AuthRequestDTO;
import com.nextmove.auth_service.dto.AuthResponseDTO;
import com.nextmove.auth_service.exception.ResourceNotFoundException;
import com.nextmove.auth_service.model.User;
import com.nextmove.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authenticateUser(request);
        User user = findUserByUsername(request.username());
        String token = generateToken(user);
        return new AuthResponseDTO(token);
    }

    private void authenticateUser(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), request.password()
                )
        );
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    private String generateToken(User user) {
        return jwtService.generateToken(user.getUsername(), user.getId());
    }

}
