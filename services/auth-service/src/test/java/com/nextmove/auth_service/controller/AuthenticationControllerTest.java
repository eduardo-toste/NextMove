package com.nextmove.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextmove.auth_service.config.MockSecurityConfig;
import com.nextmove.auth_service.dto.AuthRequestDTO;
import com.nextmove.auth_service.dto.AuthResponseDTO;
import com.nextmove.auth_service.dto.RegisterRequestDTO;
import com.nextmove.auth_service.service.AuthenticationService;
import com.nextmove.auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MockSecurityConfig.class)
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void resetMocks() {
        reset(authenticationService, userService);
    }

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        var request = new AuthRequestDTO("user@test.com", "password");
        var response = new AuthResponseDTO("mocked-jwt-token");

        when(authenticationService.authenticate(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));

        verify(authenticationService).authenticate(any());
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        var request = new RegisterRequestDTO("user", "user@email.com", "password");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userService).registerUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsMissingInLogin() throws Exception {
        var invalidRequest = new AuthRequestDTO("", "password");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsMissingInRegister() throws Exception {
        var invalidRequest = new RegisterRequestDTO("", "email@test.com", "pass");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}