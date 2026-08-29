package com.in6206.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AnalyticsSummaryDto(
        LocalDate from,
        LocalDate to,
        long transactionCount,
        BigDecimal totalAmount,
        BigDecimal averageAmount,
        String topCategory,
        List<CategoryTotalDto> categoryBreakdown,
        List<MonthlyTotalDto> monthlyTrend
) {
}
