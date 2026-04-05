package com.example.financeProcessing.transaction;

import com.example.financeProcessing.audit.AuditLog;
import com.example.financeProcessing.audit.AuditRepository;
import com.example.financeProcessing.common.util.SecurityUtils;
import com.example.financeProcessing.transaction.dto.*;
import com.example.financeProcessing.user.User;
import com.example.financeProcessing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository    categoryRepository;
    private final UserRepository        userRepository;
    private final AuditRepository       auditRepository;


    public Page<TransactionResponse> getAll(TransactionFilterRequest filter,
                                            int page, int size, String sortBy) {

        Specification<Transaction> spec = TransactionSpecification.withFilters(filter);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        return transactionRepository.findAll(spec, pageable)
                .map(TransactionResponse::from);
    }

    public TransactionResponse getById(UUID id) {
        return TransactionResponse.from(findOrThrow(id));
    }


    public TransactionResponse create(TransactionRequest request) {
        UUID actorId = SecurityUtils.getCurrentUserId();
        User actor   = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Category category = categoryRepository
                .findByNameIgnoreCase(request.getCategory())
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name(request.getCategory()).build()
                ));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(category)
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .user(actor)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        audit(actor, "CREATE_TRANSACTION", "Transaction", saved.getId().toString());

        return TransactionResponse.from(saved);
    }


    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = findOrThrow(id);

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            transaction.setType(request.getType());
        }
        if (request.getTransactionDate() != null) {
            transaction.setTransactionDate(request.getTransactionDate());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository
                    .findByNameIgnoreCase(request.getCategory())
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder().name(request.getCategory()).build()
                    ));
            transaction.setCategory(category);
        }

        Transaction saved = transactionRepository.save(transaction);

        UUID actorId = SecurityUtils.getCurrentUserId();
        User actor   = userRepository.findById(actorId).orElseThrow();
        audit(actor, "UPDATE_TRANSACTION", "Transaction", id.toString());

        return TransactionResponse.from(saved);
    }


    public void delete(UUID id) {
        Transaction transaction = findOrThrow(id);

        transaction.setDeleted(true);
        transactionRepository.save(transaction);

        UUID actorId = SecurityUtils.getCurrentUserId();
        User actor   = userRepository.findById(actorId).orElseThrow();
        audit(actor, "DELETE_TRANSACTION", "Transaction", id.toString());
    }



    private Transaction findOrThrow(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    private void audit(User actor, String action, String entityType, String entityId) {
        auditRepository.save(AuditLog.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .build());
    }
}