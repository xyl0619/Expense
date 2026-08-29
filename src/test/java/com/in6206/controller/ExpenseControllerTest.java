package com.in6206.controller;

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
}
