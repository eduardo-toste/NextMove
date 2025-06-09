package com.nextmove.transaction_service.dto;

import com.nextmove.transaction_service.model.enums.Status;
import com.nextmove.transaction_service.model.enums.TransactionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionPatchRequestDTO(

        String title,
        String description,
        @Positive BigDecimal amount,
        @Future LocalDate dueDate,
        TransactionType type,
        Status status

) {

}
