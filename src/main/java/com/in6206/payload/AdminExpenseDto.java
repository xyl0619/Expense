package com.in6206.payload;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminExpenseDto(
        Long id,
        String username,
        BigDecimal amount,
        String category,
        LocalDate expenseDate,
        String description
) {
}
