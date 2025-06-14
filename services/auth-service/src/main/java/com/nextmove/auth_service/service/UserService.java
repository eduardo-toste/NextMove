package com.nextmove.auth_service.service;

import com.nextmove.auth_service.dto.RegisterRequestDTO;
import com.nextmove.auth_service.dto.UserCreatedEvent;
import com.nextmove.auth_service.exception.ExistentUserException;
import com.nextmove.auth_service.model.User;
import com.nextmove.auth_service.producer.UserEventProducer;
import com.nextmove.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    public void registerUser(RegisterRequestDTO request) {
        var searchedUser = userRepository.findByUsername(request.username());

        if (searchedUser.isPresent()){
            throw new ExistentUserException("User already registered!");
        }

        var encodedPassword = passwordEncoder.encode(request.password());

        var user = new User(
                null,
                request.name(),
                request.username(),
                encodedPassword
        );

        UserCreatedEvent event = new UserCreatedEvent(user.getName(), user.getUsername());
        userEventProducer.sendUserCreatedEvent(event);

        userRepository.save(user);
    }
}
