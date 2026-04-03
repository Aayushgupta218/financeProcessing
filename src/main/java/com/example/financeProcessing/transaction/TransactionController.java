package com.example.financeProcessing.transaction;

import com.example.financeProcessing.common.response.ApiResponse;
import com.example.financeProcessing.transaction.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ALL roles can read — VIEWER, ANALYST, ADMIN
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAll(
            @ModelAttribute TransactionFilterRequest filter,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy) {

        // @ModelAttribute binds ?type=INCOME&category=Food directly to the filter object
        // No manual request.getParameter() needed
        Page<TransactionResponse> result = transactionService.getAll(filter, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getById(id)));
    }

    // Only ANALYST and ADMIN can create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response = transactionService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)        // 201, not 200 — semantically correct
                .body(ApiResponse.success("Transaction created", response));
    }

    // Only ANALYST and ADMIN can update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransactionRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Transaction updated", transactionService.update(id, request)));
    }

    // Only ADMIN can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted", null));
    }
}