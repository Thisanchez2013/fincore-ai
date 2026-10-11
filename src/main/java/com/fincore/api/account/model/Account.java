package com.fincore.api.account.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Account(
        UUID id,
        String name,
        AccountType type,
        BigDecimal balance
) {
}
