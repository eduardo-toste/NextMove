package com.nextmove.transaction_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextmove.transaction_service.dto.TransactionPatchRequestDTO;
import com.nextmove.transaction_service.dto.TransactionPutRequestDTO;
import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.model.enums.Status;
import com.nextmove.transaction_service.model.enums.TransactionType;
import com.nextmove.transaction_service.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(TransactionControllerTest.Config.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID transactionId;
    private TransactionResponseDTO mockResponse;

    @TestConfiguration
    static class Config {
        @Bean
        public TransactionService transactionService() {
            return Mockito.mock(TransactionService.class);
        }
    }

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        mockResponse = new TransactionResponseDTO(
                transactionId,
                "Aluguel",
                "Pagamento mensal do aluguel",
                new BigDecimal("1200.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                TransactionType.EXPENSE,
                Status.PENDING,
                userId
        );
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO(
                "Aluguel",
                "Pagamento mensal do aluguel",
                new BigDecimal("1200.00"),
                LocalDate.now().plusDays(5),
                TransactionType.EXPENSE
        );

        Mockito.when(transactionService.createTransaction(eq(userId), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/transaction")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.title").value("Aluguel"))
                .andExpect(jsonPath("$.description").value("Pagamento mensal do aluguel"))
                .andExpect(jsonPath("$.amount").value(1200.00))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void shouldListAllTransactions() throws Exception {
        Mockito.when(transactionService.getAllTransactions(eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(mockResponse)));

        mockMvc.perform(get("/transaction")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Aluguel"));
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        Mockito.when(transactionService.getTransactionById(userId, transactionId)).thenReturn(mockResponse);

        mockMvc.perform(get("/transaction/{id}", transactionId)
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.title").value("Aluguel"));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        TransactionPutRequestDTO request = new TransactionPutRequestDTO(
                "Aluguel Atualizado",
                "Novo valor de aluguel",
                new BigDecimal("1300.00"),
                LocalDate.now().plusDays(3),
                TransactionType.EXPENSE,
                Status.COMPLETED
        );

        Mockito.when(transactionService.updateTransaction(eq(userId), eq(transactionId), any()))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/transaction/{id}", transactionId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()));
    }

    @Test
    void shouldPatchTransaction() throws Exception {
        TransactionPatchRequestDTO patchRequest = new TransactionPatchRequestDTO(
                "Patch Aluguel", null, null, null, null, null
        );

        Mockito.when(transactionService.patchTransaction(eq(userId), eq(transactionId), any()))
                .thenReturn(mockResponse);

        mockMvc.perform(patch("/transaction/{id}", transactionId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/transaction/{id}", transactionId)
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldTriggerReminders() throws Exception {
        mockMvc.perform(post("/transaction/reminders"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reminders triggered successfully"));
    }
}