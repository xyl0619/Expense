package com.in6206.service;

import com.in6206.exception.InvalidRequestException;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.AnalyticsSummaryDto;
import com.in6206.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void summarizeBuildsCategoryPercentagesAndZeroFilledMonthlyTrend() {
        User user = new User();
        user.setId(1L);
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 31);
        when(expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(1L, from, to))
                .thenReturn(List.of(
                        expense("Food", "20.00", LocalDate.of(2026, 1, 3)),
                        expense("Food", "30.00", LocalDate.of(2026, 1, 10)),
                        expense("Travel", "50.00", LocalDate.of(2026, 3, 5))
                ));

        AnalyticsSummaryDto summary = analyticsService.summarize(user, from, to);

        assertThat(summary.totalAmount()).isEqualByComparingTo("100.00");
        assertThat(summary.averageAmount()).isEqualByComparingTo("33.33");
        assertThat(summary.transactionCount()).isEqualTo(3);
        assertThat(summary.categoryBreakdown()).hasSize(2);
        assertThat(summary.categoryBreakdown().get(0).percentage()).isEqualByComparingTo("50.00");
        assertThat(summary.monthlyTrend()).extracting(item -> item.totalAmount().toPlainString())
                .containsExactly("50.00", "0", "50.00");
    }

    @Test
    void summarizeRejectsRangesLongerThanFiveYears() {
        User user = new User();
        user.setId(1L);

        assertThatThrownBy(() -> analyticsService.summarize(
                user, LocalDate.of(2020, 1, 1), LocalDate.of(2026, 1, 2)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("five years");
    }

    private Expense expense(String category, String amount, LocalDate date) {
        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(new BigDecimal(amount));
        expense.setExpenseDate(date);
        return expense;
    }
}
