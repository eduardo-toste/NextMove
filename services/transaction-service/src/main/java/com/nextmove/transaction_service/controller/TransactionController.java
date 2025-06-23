package com.nextmove.transaction_service.controller;

import com.nextmove.transaction_service.dto.*;
import com.nextmove.transaction_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Transactions", description = "Endpoints for managing user transactions")
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Create a new transaction",
            description = "Creates and persists a new transaction for the given user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Parameter(description = "User ID", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody TransactionRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(201).body(transaction);
    }

    @Operation(
            summary = "List all user transactions",
            description = "Retrieves a paginated list of transactions for the specified user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of transactions retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(
            @Parameter(description = "User ID", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable) {

        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a specific transaction by its ID for the specified user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
            }
    )
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @Parameter(description = "User ID", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable UUID transactionId) {

        TransactionResponseDTO transaction = transactionService.getTransactionById(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Fully update a transaction",
            description = "Performs a full update (PUT) of a transaction.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
            }
    )
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId,
            @RequestBody TransactionPutRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Partially update a transaction",
            description = "Updates specific fields of a transaction using PATCH.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
            }
    )
    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> partialUpdateTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId,
            @RequestBody TransactionPatchRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.patchTransaction(userId, transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Deletes a transaction by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transaction deleted"),
                    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
            }
    )
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID transactionId) {

        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Trigger reminders for pending transactions",
            description = "Sends reminder emails for transactions with upcoming due dates.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reminders sent")
            }
    )
    @PostMapping("/reminders")
    public ResponseEntity<String> triggerReminders() {
        transactionService.sendRemindersForPendingTransactions();
        return ResponseEntity.ok("Reminders triggered successfully");
    }
}