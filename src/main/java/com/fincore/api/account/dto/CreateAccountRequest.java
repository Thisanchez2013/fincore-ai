
package com.fincore.api.account.dto;

import com.fincore.api.account.model.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "O nome da conta e obrigatorio")
        String name,

        @NotNull(message = "O tipo da conta e obrigatorio")
        AccountType type,

        @NotNull(message = "O saldo inicial e obrigatorio")
        @DecimalMin(
                value = "0.00",
                message = "O saldo inicial nao pode ser negativo"
        )
        BigDecimal balance

) {
}
