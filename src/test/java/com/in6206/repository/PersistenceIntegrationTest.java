package com.in6206.repository;

import com.in6206.model.Budget;
import com.in6206.model.Expense;
import com.in6206.model.Role;
import com.in6206.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    void flywaySchemaPersistsUsersExpensesAndBudgets() {
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow();
        User user = new User();
        user.setUsername("integration-user");
        user.setEmail("integration@example.com");
        user.setPassword("bcrypt-hash-placeholder");
        user.setRoles(Set.of(role));
        user = userRepository.saveAndFlush(user);

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(new BigDecimal("42.50"));
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.of(2026, 8, 15));
        expenseRepository.saveAndFlush(expense);

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory("Food");
        budget.setBudgetMonth(LocalDate.of(2026, 8, 1));
        budget.setLimitAmount(new BigDecimal("300.00"));
        budgetRepository.saveAndFlush(budget);

        assertThat(expenseRepository.findByUserId(user.getId())).hasSize(1);
        assertThat(budgetRepository.findByUserIdAndBudgetMonthOrderByCategoryAsc(
                user.getId(), LocalDate.of(2026, 8, 1))).hasSize(1);
    }
}
