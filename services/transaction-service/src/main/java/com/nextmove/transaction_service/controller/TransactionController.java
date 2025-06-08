package com.nextmove.transaction_service.controller;

import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(@RequestHeader("X-User-Id") String userIdHeader, Pageable pageable) {
        UUID userId = UUID.fromString(userIdHeader);
        Page<TransactionResponseDTO> transactions =  transactionService.getAllTransactions(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

}
