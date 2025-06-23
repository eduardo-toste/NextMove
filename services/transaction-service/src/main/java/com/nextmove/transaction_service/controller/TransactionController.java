package com.nextmove.transaction_service.controller;

import com.nextmove.transaction_service.dto.TransactionPatchRequestDTO;
import com.nextmove.transaction_service.dto.TransactionPutRequestDTO;
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
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody TransactionRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable) {

        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId) {

        TransactionResponseDTO transaction = transactionService.getTransactionById(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId,
            @RequestBody TransactionPutRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> partialUpdateTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId,
            @RequestBody TransactionPatchRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.patchTransaction(userId, transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId) {

        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reminders")
    public ResponseEntity<String> triggerReminders() {
        transactionService.sendRemindersForPendingTransactions();
        return ResponseEntity.ok("Reminders triggered successfully");
    }
}