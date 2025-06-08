package com.nextmove.transaction_service.repository;

import com.nextmove.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {



}
