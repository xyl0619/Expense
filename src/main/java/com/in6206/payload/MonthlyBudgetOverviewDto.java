package com.in6206.payload;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlyBudgetOverviewDto(
        YearMonth month,
        BigDecimal totalLimit,
        BigDecimal totalSpent,
        BigDecimal totalRemaining,
        long exceededCount,
        List<BudgetStatusDto> budgets
) {
}
