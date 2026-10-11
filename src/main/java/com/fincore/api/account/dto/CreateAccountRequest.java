package com.fincore.api.account.dto;

import com.fincore.api.account.model.AccountType;
import java.math.BigDecimal;

public record CreateAccountRequest(
        String name,
        AccountType type,
        BigDecimal balance
) {
}
