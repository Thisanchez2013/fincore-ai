
package com.fincore.api.transaction.dto;

import com.fincore.api.transaction.model.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(

        @NotBlank(message = "A descricao e obrigatoria")
        String description,

        @NotNull(message = "O valor e obrigatorio")
        @DecimalMin(
                value = "0.01",
                message = "O valor deve ser maior que zero"
        )
        BigDecimal amount,

        @NotNull(message = "O tipo da transacao e obrigatorio")
        TransactionType type,

        @NotNull(message = "A conta e obrigatoria")
        UUID accountId

) {
}
