package com.nextmove.transaction_service.dto;

import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDTO(

        UUID id,
        String title,
        String description,
        BigDecimal amount,
        LocalDate createdAt,
        LocalDate dueDate,
        TransactionType type,
        UUID userId

) {

    public TransactionResponseDTO(Transaction transaction){
        this(transaction.getId(), transaction.getTitle(), transaction.getDescription(), transaction.getAmount(),
                transaction.getCreatedAt(), transaction.getDueDate(), transaction.getType(), transaction.getUserId());
    }

}
