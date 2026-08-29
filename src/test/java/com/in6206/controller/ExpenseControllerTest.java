package com.in6206.controller;

import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.repository.UserRepository;
import com.in6206.security.UserDetailsImpl;
import com.in6206.service.ExpenseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseController expenseController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteExpenseRejectsRecordOwnedByAnotherUser() {
        User currentUser = user(1L, "current");
        User otherUser = user(2L, "other");
        Expense expense = new Expense();
        expense.setId(10L);
        expense.setUser(otherUser);
        authenticate(currentUser);
        when(expenseService.getExpenseById(10L)).thenReturn(expense);

        ResponseEntity<Void> response = expenseController.deleteExpense(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(expenseService, never()).deleteExpense(10L);
    }

    @Test
    void deleteExpenseAllowsRecordOwner() {
        User currentUser = user(1L, "current");
        Expense expense = new Expense();
        expense.setId(10L);
        expense.setUser(currentUser);
        authenticate(currentUser);
        when(expenseService.getExpenseById(10L)).thenReturn(expense);

        ResponseEntity<Void> response = expenseController.deleteExpense(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expenseService).deleteExpense(10L);
    }

    private void authenticate(User user) {
        UserDetailsImpl principal = new UserDetailsImpl(
                user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), List.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("hashed-password");
        return user;
    }
}
