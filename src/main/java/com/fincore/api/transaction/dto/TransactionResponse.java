
package com.fincore.api.transaction.dto;

import com.fincore.api.transaction.model.Transaction;
import com.fincore.api.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime createdAt,
        UUID accountId
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.getAccount().getId()
        );
    }
}
