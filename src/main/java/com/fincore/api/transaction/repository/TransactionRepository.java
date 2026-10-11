
package com.fincore.api.transaction.repository;

import com.fincore.api.transaction.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByAccountId(
            UUID accountId,
            Pageable pageable
    );

    Page<Transaction> findByType(
            com.fincore.api.transaction.model.TransactionType type,
            Pageable pageable
    );

    Page<Transaction> findByAccountIdAndType(
            UUID accountId,
            com.fincore.api.transaction.model.TransactionType type,
            Pageable pageable
    );
}
