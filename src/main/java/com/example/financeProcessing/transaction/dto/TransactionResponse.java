package com.example.financeProcessing.transaction.dto;

import com.example.financeProcessing.common.enums.TransactionType;
import com.example.financeProcessing.transaction.Transaction;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TransactionResponse {

    private final UUID id;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;
    private final String description;
    private final LocalDate transactionDate;
    private final LocalDateTime createdAt;

    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t);
    }

    private TransactionResponse(Transaction t) {
        this.id              = t.getId();
        this.amount          = t.getAmount();
        this.type            = t.getType();
        this.category        = t.getCategory() != null ? t.getCategory().getName() : null;
        this.description     = t.getDescription();
        this.transactionDate = t.getTransactionDate();
        this.createdAt       = t.getCreatedAt();
    }
}