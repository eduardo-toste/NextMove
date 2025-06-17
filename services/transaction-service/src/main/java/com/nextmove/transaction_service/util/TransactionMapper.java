package com.nextmove.transaction_service.util;

import com.nextmove.transaction_service.dto.TransactionReminderEvent;
import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.dto.UserResponseDTO;
import com.nextmove.transaction_service.model.Transaction;
import com.nextmove.transaction_service.model.enums.Status;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionMapper {

    public static Transaction toEntity(TransactionRequestDTO dto, UUID userId) {
        return new Transaction(
                null,
                dto.title(),
                dto.description(),
                dto.amount(),
                LocalDate.now(),
                dto.dueDate(),
                dto.type(),
                Status.PENDING,
                userId
        );
    }

    public static TransactionResponseDTO toDTO(Transaction transaction) {
        return new TransactionResponseDTO(transaction);
    }

    public static Page<TransactionResponseDTO> toDTOPage(Page<Transaction> page) {
        return page.map(TransactionResponseDTO::new);
    }

    public static TransactionReminderEvent toEvent(UserResponseDTO user, Transaction transaction) {
        return new TransactionReminderEvent(
                user.name(),
                user.username(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getDueDate(),
                transaction.getAmount(),
                transaction.getType().toString()
        );
    }

}
