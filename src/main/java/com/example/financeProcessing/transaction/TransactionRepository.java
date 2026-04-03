package com.example.financeProcessing.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID>,
        JpaSpecificationExecutor<Transaction> {
    // JpaSpecificationExecutor adds:
    // Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable)
    // You get dynamic filtering + pagination in one call — no custom SQL needed
}