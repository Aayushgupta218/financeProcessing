package com.example.financeProcessing.transaction.dto;

import com.example.financeProcessing.common.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionFilterRequest {
    private TransactionType type;
    private String category;
    private LocalDate from;
    private LocalDate to;
    private String description;
}