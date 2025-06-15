package com.nextmove.transaction_service.dto;

import com.nextmove.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionReminderEvent(

        String name,
        String email,
        String title,
        String description,
        LocalDate dueDate,
        BigDecimal amount,
        TransactionType type

)
{

}
