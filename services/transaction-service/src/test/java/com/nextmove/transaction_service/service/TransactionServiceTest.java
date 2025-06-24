package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.client.UserClient;
import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.model.enums.TransactionType;
import com.nextmove.transaction_service.producer.TransactionReminderProducer;
import com.nextmove.transaction_service.repository.TransactionRepository;
import com.nextmove.transaction_service.util.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private TransactionReminderProducer transactionReminderProducer;

    private UUID userId;
    private TransactionRequestDTO request;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        request = new TransactionRequestDTO(
                "Salário",
                "Salário do mês atual",
                new BigDecimal("5000.0"),
                LocalDate.of(2025, 7, 5),
                TransactionType.INCOME
        );
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Arrange
        Transaction transaction = TransactionMapper.toEntity(request, userId);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionResponseDTO response = transactionService.createTransaction(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Salário", response.title());
        assertEquals(new BigDecimal("5000.0"), response.amount());
        assertEquals("INCOME", response.type().name());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}