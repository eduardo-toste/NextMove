package com.nextmove.transaction_service.service;

import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@Service
public class TransactionService {

    public Page<TransactionResponseDTO> getAllTransactions(String username, Pageable pageable) {
        return null;
    }

}
