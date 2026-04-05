package com.example.financeProcessing.transaction;

import com.example.financeProcessing.transaction.dto.TransactionFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    private TransactionSpecification() {}

    public static Specification<Transaction> withFilters(TransactionFilterRequest filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getType() != null) {
                predicates.add(
                        cb.equal(root.get("type"), filter.getType())
                );
            }

            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
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
                predicates.add(
                        cb.like(
                                cb.lower(root.get("description")),
                                "%" + filter.getDescription().toLowerCase() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}