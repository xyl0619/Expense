package com.in6206.service;

import com.in6206.exception.ForbiddenOperationException;
import com.in6206.exception.InvalidRequestException;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.ExpenseDto;
import com.in6206.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void deleteExpenseRejectsRecordOwnedByAnotherUser() {
        User currentUser = user(1L);
        Expense expense = new Expense();
        expense.setId(10L);
        expense.setUser(user(2L));
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.deleteExpense(10L, currentUser))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("another user's expense");
        verify(expenseRepository, never()).delete(expense);
    }

    @Test
    void deleteExpenseAllowsRecordOwner() {
        User currentUser = user(1L);
        Expense expense = new Expense();
        expense.setId(10L);
        expense.setUser(currentUser);
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(10L, currentUser);

        verify(expenseRepository).delete(expense);
    }

    @Test
    void createExpenseNormalizesUserInput() {
        User currentUser = user(1L);
        ExpenseDto request = new ExpenseDto(
                null, new BigDecimal("12.50"), "  Food  ", LocalDate.of(2026, 8, 20), "  Lunch  ");
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense saved = expenseService.createExpense(request, currentUser);

        assertThat(saved.getCategory()).isEqualTo("Food");
        assertThat(saved.getDescription()).isEqualTo("Lunch");
        assertThat(saved.getUser()).isSameAs(currentUser);
    }

    @Test
    void searchRejectsReversedDateAndAmountRanges() {
        assertThatThrownBy(() -> expenseService.searchExpenses(
                1L, null,
                LocalDate.of(2026, 8, 2), LocalDate.of(2026, 8, 1),
                null, null, Pageable.unpaged()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("start date");

        assertThatThrownBy(() -> expenseService.searchExpenses(
                1L, null, null, null,
                new BigDecimal("50"), new BigDecimal("10"), Pageable.unpaged()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Minimum amount");
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
