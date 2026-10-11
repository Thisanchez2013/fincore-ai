
package com.fincore.api.transaction.dto;

import com.fincore.api.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        String description,
        BigDecimal amount,
        TransactionType type,
        UUID accountId
) {
}
