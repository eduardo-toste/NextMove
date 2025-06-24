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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.nextmove.transaction_service.util.BuilderUtil.buildTransactionRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private TransactionRequestDTO salaryRequest;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        salaryRequest = buildTransactionRequest("Salário", "Salário do mês atual", "5000.0", TransactionType.INCOME);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Arrange
        Transaction transaction = TransactionMapper.toEntity(salaryRequest, userId);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionResponseDTO response = transactionService.createTransaction(userId, salaryRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Salário", response.title());
        assertEquals(new BigDecimal("5000.0"), response.amount());
        assertEquals("INCOME", response.type().name());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldReturnAllTransactionsFromUser() {
        // Arrange
        Pageable pageable = Pageable.ofSize(2).withPage(0);

        TransactionRequestDTO internetRequest = buildTransactionRequest("Internet", "Plano mensal", "150", TransactionType.EXPENSE);
        TransactionRequestDTO waterRequest = buildTransactionRequest("Água", "Conta de água", "80", TransactionType.EXPENSE);

        Transaction internetTx = TransactionMapper.toEntity(internetRequest, userId);
        Transaction aguaTx = TransactionMapper.toEntity(waterRequest, userId);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(internetTx, aguaTx), pageable, 2);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(transactionPage);

        // Act
        Page<TransactionResponseDTO> result = transactionService.getAllTransactions(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Internet", result.getContent().get(0).title());
        assertEquals("Água", result.getContent().get(1).title());

        verify(transactionRepository, times(1)).findByUserId(userId, pageable);
    }

}