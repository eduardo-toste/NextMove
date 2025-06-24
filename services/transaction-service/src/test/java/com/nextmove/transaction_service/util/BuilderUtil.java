package com.nextmove.transaction_service.util;

import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BuilderUtil {

    public static TransactionRequestDTO buildTransactionRequest(String title, String description, String amount, TransactionType type) {
        return new TransactionRequestDTO(
                title,
                description,
                new BigDecimal(amount),
                LocalDate.of(2025, 7, 5),
                type
        );
    }

}
