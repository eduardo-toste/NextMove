package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.client.UserClient;
import com.nextmove.transaction_service.dto.*;
import com.nextmove.transaction_service.exception.ResourceNotFoundException;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.producer.TransactionReminderProducer;
import com.nextmove.transaction_service.repository.TransactionRepository;
import com.nextmove.transaction_service.util.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final UserClient userClient;
    private final TransactionReminderProducer transactionReminderProducer;

    public TransactionResponseDTO createTransaction(UUID userId, TransactionRequestDTO request) {
        Transaction transaction = TransactionMapper.toEntity(request, userId);
        repository.save(transaction);

        return TransactionMapper.toDTO(transaction);
    }

    public Page<TransactionResponseDTO> getAllTransactions(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = repository.findByUserId(userId, pageable);

        return TransactionMapper.toDTOPage(transactions);
    }

    public TransactionResponseDTO getTransactionById(UUID userId, UUID transactionId) {
        Transaction transaction = getTransactionOrThrow(userId, transactionId);

        return TransactionMapper.toDTO(transaction);
    }

    public TransactionResponseDTO updateTransaction(UUID userId, UUID transactionId, TransactionPutRequestDTO request) {
        Transaction transaction = getTransactionOrThrow(userId, transactionId);
        transaction.putUpdate(request);
        repository.save(transaction);

        return TransactionMapper.toDTO(transaction);
    }

    public TransactionResponseDTO patchTransaction(UUID userId, UUID transactionId, TransactionPatchRequestDTO request) {
        Transaction transaction = getTransactionOrThrow(userId, transactionId);
        transaction.patchUpdate(request);
        repository.save(transaction);

        return TransactionMapper.toDTO(transaction);
    }

    public void deleteTransaction(UUID userId, UUID transactionId) {
        Transaction transaction = getTransactionOrThrow(userId, transactionId);

        repository.delete(transaction);
    }

    public void sendRemindersForPendingTransactions() {
        LocalDate date = LocalDate.now().plusDays(2);
        List<Transaction> transactions = repository.findByDueDate(date);

        transactions.forEach(transaction -> {
            UserResponseDTO user = userClient.getUserById(transaction.getUserId());
            TransactionReminderEvent event = TransactionMapper.toEvent(user, transaction);

            transactionReminderProducer.sendTransactionReminderEvent(event);
        });
    }

    private Transaction getTransactionOrThrow(UUID userId, UUID transactionId){
        Transaction transaction = repository.findByUserIdAndId(userId, transactionId);

        if(transaction == null){
            throw new ResourceNotFoundException("Transaction not found!");
        }

        return transaction;
    }
}
