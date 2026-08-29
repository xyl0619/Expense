package com.in6206.service;

import com.in6206.exception.ForbiddenOperationException;
import com.in6206.model.Budget;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.BudgetRequest;
import com.in6206.payload.BudgetStatusDto;
import com.in6206.repository.BudgetRepository;
import com.in6206.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void upsertBudgetReturnsUtilizationAndExceededState() {
        User user = new User();
        user.setId(1L);
        YearMonth month = YearMonth.of(2026, 8);
        BudgetRequest request = new BudgetRequest("Food", new BigDecimal("100.00"), month);
        when(budgetRepository.findByUserIdAndCategoryIgnoreCaseAndBudgetMonth(
                1L, "Food", month.atDay(1))).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget budget = invocation.getArgument(0);
            budget.setId(8L);
            return budget;
        });
        when(expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
                1L, month.atDay(1), month.atEndOfMonth()))
                .thenReturn(List.of(expense("Food", "125.00", LocalDate.of(2026, 8, 2))));

        BudgetStatusDto result = budgetService.upsertBudget(user, request);

        assertThat(result.id()).isEqualTo(8L);
        assertThat(result.spentAmount()).isEqualByComparingTo("125.00");
        assertThat(result.remainingAmount()).isEqualByComparingTo("-25.00");
        assertThat(result.utilizationPercent()).isEqualByComparingTo("125.00");
        assertThat(result.exceeded()).isTrue();
    }

    @Test
    void deleteBudgetRejectsAnotherUsersBudget() {
        User currentUser = new User();
        currentUser.setId(1L);
        User owner = new User();
        owner.setId(2L);
        Budget budget = new Budget();
        budget.setId(9L);
        budget.setUser(owner);
        when(budgetRepository.findById(9L)).thenReturn(Optional.of(budget));

        assertThatThrownBy(() -> budgetService.deleteBudget(currentUser, 9L))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("another user's budget");
        verify(budgetRepository, never()).delete(budget);
    }

    private Expense expense(String category, String amount, LocalDate date) {
        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(new BigDecimal(amount));
        expense.setExpenseDate(date);
        return expense;
    }
}
