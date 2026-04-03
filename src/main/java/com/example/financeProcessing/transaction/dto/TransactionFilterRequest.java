package com.example.financeProcessing.transaction.dto;

import com.example.financeProcessing.common.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

// This is NOT a request body — it's populated from query params via @ModelAttribute
@Getter
@Setter
public class TransactionFilterRequest {
    private TransactionType type;       // ?type=INCOME
    private String category;            // ?category=Food
    private LocalDate from;             // ?from=2024-01-01
    private LocalDate to;               // ?to=2024-12-31
    private String description;         // ?description=salary  (keyword search)
}