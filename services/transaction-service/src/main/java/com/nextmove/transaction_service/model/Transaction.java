package com.nextmove.transaction_service.model;

import com.nextmove.transaction_service.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "Transaction")
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String title;

    @Column(nullable = false)
    @Setter
    private String description;

    @Column(nullable = false)
    @Setter
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "due_date", nullable = false)
    @Setter
    private LocalDate dueDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private TransactionType type;

    @Column(name = "user_id", nullable = false)
    private UUID userId;


}
