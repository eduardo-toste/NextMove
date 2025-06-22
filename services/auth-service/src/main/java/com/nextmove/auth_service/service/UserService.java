package com.nextmove.auth_service.service;

import com.nextmove.auth_service.dto.RegisterRequestDTO;
import com.nextmove.auth_service.dto.UserCreatedEvent;
import com.nextmove.auth_service.dto.UserResponseDTO;
import com.nextmove.auth_service.exception.ExistentUserException;
import com.nextmove.auth_service.exception.ResourceNotFoundException;
import com.nextmove.auth_service.model.User;
import com.nextmove.auth_service.producer.UserEventProducer;
import com.nextmove.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    public void registerUser(RegisterRequestDTO request) {
        validateUserDoesNotExist(request.username());
        var user = createUserFromRequest(request);
        userRepository.save(user);
        publishUserCreatedEvent(user);
    }

    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Não encontramos nenhuma conta com o e-mail informado."));

        return new UserResponseDTO(user);
    }

    private void validateUserDoesNotExist(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ExistentUserException("O e-mail informado já está cadastrado. Por favor, tente outro.");
        }
    }

    private User createUserFromRequest(RegisterRequestDTO request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        return new User(null, request.name(), request.username(), encodedPassword);
    }

    private void publishUserCreatedEvent(User user) {
        var event = new UserCreatedEvent(user.getName(), user.getUsername());
        userEventProducer.sendUserCreatedEvent(event);
    }
}
