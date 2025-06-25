package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.client.UserClient;
import com.nextmove.transaction_service.dto.TransactionPatchRequestDTO;
import com.nextmove.transaction_service.dto.TransactionPutRequestDTO;
import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.exception.ResourceNotFoundException;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.model.enums.Status;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.nextmove.transaction_service.util.BuilderUtil.buildTransactionRequest;
import static com.nextmove.transaction_service.util.TransactionMapper.toEntity;
import static org.junit.jupiter.api.Assertions.*;
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
    private UUID transactionId;
    private TransactionRequestDTO salaryRequest;
    private TransactionRequestDTO internetRequest;
    private TransactionRequestDTO waterRequest;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        salaryRequest = buildTransactionRequest("Salário", "Salário do mês atual", "5000.0", TransactionType.INCOME);
        internetRequest = buildTransactionRequest("Internet", "Plano mensal", "150", TransactionType.EXPENSE);
        waterRequest = buildTransactionRequest("Água", "Conta de água", "80", TransactionType.EXPENSE);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Arrange
        Transaction transaction = toEntity(salaryRequest, userId);
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

        Transaction internetTx = toEntity(internetRequest, userId);
        Transaction aguaTx = toEntity(waterRequest, userId);

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

    @Test
    void shouldReturnSpecificTransaction() {
        // Arrange
        Transaction transaction = toEntity(salaryRequest, userId);
        transaction.setId(transactionId);

        when(transactionRepository.findByUserIdAndId(userId, transactionId)).thenReturn(transaction);

        // Act
        TransactionResponseDTO result = transactionService.getTransactionById(userId, transactionId);

        // Assert
        assertNotNull(result);
        assertEquals("Salário", result.title());
        assertEquals(transactionId, result.id());
        verify(transactionRepository, times(1)).findByUserIdAndId(userId, transactionId);
    }

    @Test
    void shouldUpdateATransaction() {
        Transaction salaryTransaction = TransactionMapper.toEntity(salaryRequest, userId);
        salaryTransaction.setId(transactionId);

        // Simula o novo DTO com alterações
        TransactionPutRequestDTO updateRequest = new TransactionPutRequestDTO(
                "Novo título",
                "Nova descrição",
                new BigDecimal("3000.0"),
                LocalDate.of(2025, 8, 1),
                TransactionType.EXPENSE,
                Status.PENDING
        );

        // Arrange
        when(transactionRepository.findByUserIdAndId(userId, transactionId)).thenReturn(salaryTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(salaryTransaction);

        // Act
        TransactionResponseDTO result = transactionService.updateTransaction(userId, transactionId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Novo título", result.title());
        assertEquals("Nova descrição", result.description());
        assertEquals(new BigDecimal("3000.0"), result.amount());
        assertEquals(TransactionType.EXPENSE, result.type());

        verify(transactionRepository).findByUserIdAndId(userId, transactionId);
        verify(transactionRepository).save(salaryTransaction);
    }

    @Test
    void shouldPatchATransaction() {
        // Arrange
        Transaction salaryTransaction = TransactionMapper.toEntity(salaryRequest, userId);
        salaryTransaction.setId(transactionId);

        TransactionPatchRequestDTO request = new TransactionPatchRequestDTO(
                null, null, new BigDecimal("6000.00"), null, null, null
        );

        // 🔥 Mock necessário para simular a busca da transação antes do patch
        when(transactionRepository.findByUserIdAndId(userId, transactionId)).thenReturn(salaryTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(salaryTransaction);

        // Act
        TransactionResponseDTO result = transactionService.patchTransaction(userId, transactionId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Salário", result.title()); // título não mudou
        assertEquals(new BigDecimal("6000.00"), result.amount()); // valor atualizado
        assertEquals(TransactionType.INCOME, result.type()); // tipo não mudou

        verify(transactionRepository).findByUserIdAndId(userId, transactionId);
        verify(transactionRepository).save(salaryTransaction);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentTransaction() {
        TransactionPutRequestDTO updateRequest = new TransactionPutRequestDTO(
                "Título", "Desc", new BigDecimal("100"), LocalDate.now(), TransactionType.EXPENSE, Status.PENDING
        );

        when(transactionRepository.findByUserIdAndId(userId, transactionId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.updateTransaction(userId, transactionId, updateRequest)
        );

        verify(transactionRepository, times(1)).findByUserIdAndId(userId, transactionId);
        verify(transactionRepository, never()).save(any());
    }

}