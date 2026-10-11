package com.fincore.api.account.dto;

import com.fincore.api.account.model.AccountType;
import java.math.BigDecimal;

public record UpdateAccountRequest(
        String name,
        AccountType type
) {
}
