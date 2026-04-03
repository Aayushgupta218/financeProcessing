package com.example.financeProcessing.transaction.dto;

import com.example.financeProcessing.common.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotNull(message = "Type is required — INCOME or EXPENSE")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(max = 80, message = "Category max 80 characters")
    private String category;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate transactionDate;

    @Size(max = 500, message = "Description max 500 characters")
    private String description;
}