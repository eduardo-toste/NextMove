package com.nextmove.notification_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionReminderEvent(

        String name,
        String email,
        String title,
        String description,
        LocalDate dueDate,
        BigDecimal amount,
        String type

)
{

}
