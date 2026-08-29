package com.in6206.service;

import com.in6206.exception.InvalidRequestException;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.AnalyticsSummaryDto;
import com.in6206.payload.CategoryTotalDto;
import com.in6206.payload.MonthlyTotalDto;
import com.in6206.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private static final long MAX_RANGE_DAYS = 1_826;

    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public AnalyticsSummaryDto summarize(User user, LocalDate from, LocalDate to) {
        validateRange(from, to);
        List<Expense> expenses = expenseRepository
                .findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(user.getId(), from, to);
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = expenses.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> categories = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        expenses.forEach(expense -> categories.merge(
                expense.getCategory(), expense.getAmount(), BigDecimal::add));
        List<CategoryTotalDto> categoryBreakdown = categories.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new CategoryTotalDto(
                        entry.getKey(),
                        entry.getValue(),
                        percentage(entry.getValue(), total)))
                .toList();

        Map<YearMonth, BigDecimal> monthlyTotals = initializeMonths(from, to);
        expenses.forEach(expense -> monthlyTotals.merge(
                YearMonth.from(expense.getExpenseDate()), expense.getAmount(), BigDecimal::add));
        List<MonthlyTotalDto> monthlyTrend = monthlyTotals.entrySet().stream()
                .map(entry -> new MonthlyTotalDto(entry.getKey(), entry.getValue()))
                .toList();

        String topCategory = categoryBreakdown.isEmpty() ? null : categoryBreakdown.get(0).category();
        return new AnalyticsSummaryDto(
                from,
                to,
                expenses.size(),
                total,
                average,
                topCategory,
                categoryBreakdown,
                monthlyTrend
        );
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidRequestException("The start date must not be after the end date");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new InvalidRequestException("Analytics date range must not exceed five years");
        }
    }

    private BigDecimal percentage(BigDecimal amount, BigDecimal total) {
        if (total.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    private Map<YearMonth, BigDecimal> initializeMonths(LocalDate from, LocalDate to) {
        Map<YearMonth, BigDecimal> months = new LinkedHashMap<>();
        YearMonth current = YearMonth.from(from);
        YearMonth end = YearMonth.from(to);
        while (!current.isAfter(end)) {
            months.put(current, BigDecimal.ZERO);
            current = current.plusMonths(1);
        }
        return months;
    }
}
