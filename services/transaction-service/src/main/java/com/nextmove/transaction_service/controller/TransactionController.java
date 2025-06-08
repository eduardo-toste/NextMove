package com.nextmove.transaction_service.controller;

import com.nextmove.transaction_service.dto.TransactionRequestDTO;
import com.nextmove.transaction_service.dto.TransactionResponseDTO;
import com.nextmove.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestHeader("X-User-Id") UUID userId, @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO transaction = transactionService.createTransaction(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactions(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID transactionId) {
        TransactionResponseDTO transaction = transactionService.getTransactionById(userId, transactionId);

        return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

}
