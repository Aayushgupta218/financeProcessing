package com.example.financeProcessing.transaction;

import com.example.financeProcessing.common.enums.TransactionType;
import com.example.financeProcessing.transaction.dto.TransactionFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    // Private constructor — this is a utility class, never instantiated
    private TransactionSpecification() {}

    public static Specification<Transaction> withFilters(TransactionFilterRequest filter) {

        return (root, query, cb) -> {

            // Start with an empty list of conditions
            List<Predicate> predicates = new ArrayList<>();

            // Each block only adds a condition IF that filter param was actually sent
            // This is the power of Specifications — zero params = no WHERE clause at all

            if (filter.getType() != null) {
                predicates.add(
                        cb.equal(root.get("type"), filter.getType())
                );
            }

            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                // Join to category table and match by name (case-insensitive)
                predicates.add(
                        cb.equal(
                                cb.lower(root.join("category").get("name")),
                                filter.getCategory().toLowerCase()
                        )
                );
            }

            if (filter.getFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.getFrom())
                );
            }

            if (filter.getTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("transactionDate"), filter.getTo())
                );
            }

            if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
                // LIKE %keyword% search on description field
                predicates.add(
                        cb.like(
                                cb.lower(root.get("description")),
                                "%" + filter.getDescription().toLowerCase() + "%"
                        )
                );
            }

            // AND all active conditions together
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}