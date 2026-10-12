
package com.fincore.api.account.dto;

import com.fincore.api.account.model.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountRequest(

        @NotBlank(message = "O nome da conta e obrigatorio")
        String name,

        @NotNull(message = "O tipo da conta e obrigatorio")
        AccountType type

) {
}
