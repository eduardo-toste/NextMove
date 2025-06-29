package com.nextmove.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextmove.auth_service.config.MockSecurityConfig;
import com.nextmove.auth_service.dto.UserResponseDTO;
import com.nextmove.auth_service.exception.ResourceNotFoundException;
import com.nextmove.auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(MockSecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    void resetMocks() {
        reset(userService);
    }

    @Test
    void shouldReturnUserSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseDTO mockUser = new UserResponseDTO("Eduardo", "eduardo@test.com");

        when(userService.getUserById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eduardo"))
                .andExpect(jsonPath("$.username").value("eduardo@test.com"));

        verify(userService).getUserById(userId);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    void shouldReturnBadRequestForInvalidUUID() throws Exception {
        String invalidUUID = "not-a-uuid";

        mockMvc.perform(get("/users/" + invalidUUID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}