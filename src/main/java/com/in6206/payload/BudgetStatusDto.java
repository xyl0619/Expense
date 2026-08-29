package com.in6206.payload;

import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetStatusDto(
        Long id,
        String category,
        YearMonth month,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercent,
        boolean exceeded
) {
}
