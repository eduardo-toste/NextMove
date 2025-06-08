package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public Page<TransactionResponseDTO> getAllTransactions(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = repository.findByUserId(userId, pageable);

        // adicionar exception caso esteja vazio aqui

        return transactions.map(TransactionResponseDTO::new);
    }

}
