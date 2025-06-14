package com.nextmove.transaction_service.repository;

import com.nextmove.transaction_service.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUserId(UUID userId, Pageable pageable);

    Transaction findByUserIdAndId(UUID userId, UUID transactionId);

    List<Transaction> findByDueDate(LocalDate dueDate);

}
