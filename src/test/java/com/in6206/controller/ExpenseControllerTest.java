package com.in6206.controller;

import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.service.CurrentUserService;
import com.in6206.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void deleteExpenseDelegatesAuthorizationToService() {
        User currentUser = new User();
        currentUser.setId(1L);
        when(currentUserService.requireCurrentUser()).thenReturn(currentUser);

        ResponseEntity<Void> response = expenseController.deleteExpense(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expenseService).deleteExpense(10L, currentUser);
    }

    @Test
    void reportHeaderUsesRequestedLanguage() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("12.50"));
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.of(2026, 9, 1));

        when(currentUserService.requireCurrentUser()).thenReturn(currentUser);
        when(expenseService.getAllExpensesByUser(1L)).thenReturn(List.of(expense));

        MockHttpServletResponse chineseResponse = new MockHttpServletResponse();
        expenseController.downloadReport("zh", chineseResponse);
        assertThat(chineseResponse.getContentAsString())
                .contains("ID,金额,分类,日期,备注");

        MockHttpServletResponse englishResponse = new MockHttpServletResponse();
        expenseController.downloadReport("en", englishResponse);
        assertThat(englishResponse.getContentAsString())
                .contains("ID,Amount,Category,Date,Description");
    }
}
