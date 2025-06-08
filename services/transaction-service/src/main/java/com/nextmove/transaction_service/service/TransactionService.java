package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.exception.ResourceNotFoundException;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionResponseDTO createTransaction(UUID userId, TransactionRequestDTO request) {
        Transaction transaction = new Transaction(
                null,
                request.title(),
                request.description(),
                request.amount(),
                LocalDate.now(),
                request.dueDate(),
                request.type(),
                userId
        );

        return new TransactionResponseDTO(repository.save(transaction));
    }

    public Page<TransactionResponseDTO> getAllTransactions(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = repository.findByUserId(userId, pageable);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("You do not have transactions registered!");
        }

        return transactions.map(TransactionResponseDTO::new);
    }
}
