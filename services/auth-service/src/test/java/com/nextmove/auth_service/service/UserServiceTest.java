package com.nextmove.auth_service.service;

import com.nextmove.auth_service.dto.RegisterRequestDTO;
import com.nextmove.auth_service.model.User;
import com.nextmove.auth_service.producer.UserEventProducer;
import com.nextmove.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventProducer userEventProducer;

    @Test
    void shouldRegisterUser() {
        RegisterRequestDTO request = new RegisterRequestDTO("Eduardo", "eduardo@test.com", "123123");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        userService.registerUser(request);

        verify(userRepository).save(any(User.class));
        verify(userEventProducer).sendUserCreatedEvent(any());
    }

}