package com.example.financeProcessing.transaction.dto;

import com.example.financeProcessing.common.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class UpdateTransactionRequest {

    // All fields optional on update — only patch what's sent
    @Positive(message = "Amount must be positive")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    private TransactionType type;

    @Size(max = 80)
    private String category;

    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate transactionDate;

    @Size(max = 500)
    private String description;
}