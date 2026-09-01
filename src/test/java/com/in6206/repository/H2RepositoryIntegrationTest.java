package com.in6206.repository;

import com.in6206.model.Budget;
import com.in6206.model.Expense;
import com.in6206.model.Role;
import com.in6206.model.User;
import com.in6206.payload.AdminExpenseDto;
import com.in6206.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@Import({UserService.class, H2RepositoryIntegrationTest.PasswordConfig.class})
class H2RepositoryIntegrationTest {

    @TestConfiguration
    static class PasswordConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    @Autowired
    H2RepositoryIntegrationTest(UserService userService,
                                UserRepository userRepository,
                                RoleRepository roleRepository,
                                ExpenseRepository expenseRepository,
                                BudgetRepository budgetRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    @Test
    void deletingUserAlsoDeletesOwnedExpensesAndBudgets() {
        User user = createUser("delete-me");

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(new BigDecimal("12.50"));
        expense.setCategory("测试");
        expense.setExpenseDate(LocalDate.now());
        expenseRepository.save(expense);

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory("测试");
        budget.setBudgetMonth(LocalDate.now().withDayOfMonth(1));
        budget.setLimitAmount(new BigDecimal("100.00"));
        budgetRepository.save(budget);

        assertThat(userService.deleteNonAdminUser(user.getId())).isTrue();
        userRepository.flush();

        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(expenseRepository.count()).isZero();
        assertThat(budgetRepository.count()).isZero();
    }

    @Test
    void adminExpenseReportIncludesOwningUsername() {
        User user = createUser("report-owner");

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(new BigDecimal("25.00"));
        expense.setCategory("交通");
        expense.setExpenseDate(LocalDate.of(2026, 9, 1));
        expenseRepository.saveAndFlush(expense);

        assertThat(expenseRepository.findAllForAdminReport())
                .extracting(AdminExpenseDto::username)
                .containsExactly("report-owner");
    }

    private User createUser(String username) {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole = roleRepository.save(userRole);

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("bcrypt-hash");
        user.setRoles(new HashSet<>(Set.of(userRole)));
        return userRepository.save(user);
    }
}
