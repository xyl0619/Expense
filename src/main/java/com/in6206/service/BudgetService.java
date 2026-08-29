package com.in6206.service;

import com.in6206.exception.ForbiddenOperationException;
import com.in6206.exception.ResourceNotFoundException;
import com.in6206.model.Budget;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.BudgetRequest;
import com.in6206.payload.BudgetStatusDto;
import com.in6206.payload.MonthlyBudgetOverviewDto;
import com.in6206.repository.BudgetRepository;
import com.in6206.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public MonthlyBudgetOverviewDto getMonthlyOverview(User user, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        List<Budget> budgets = budgetRepository
                .findByUserIdAndBudgetMonthOrderByCategoryAsc(user.getId(), monthStart);
        Map<String, BigDecimal> spending = spendingByCategory(user.getId(), month);

        List<BudgetStatusDto> statuses = budgets.stream()
                .map(budget -> toStatus(budget, spending.getOrDefault(budget.getCategory(), BigDecimal.ZERO)))
                .toList();
        BigDecimal totalLimit = statuses.stream()
                .map(BudgetStatusDto::limitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSpent = statuses.stream()
                .map(BudgetStatusDto::spentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyBudgetOverviewDto(
                month,
                totalLimit,
                totalSpent,
                totalLimit.subtract(totalSpent),
                statuses.stream().filter(BudgetStatusDto::exceeded).count(),
                statuses
        );
    }

    @Transactional
    public BudgetStatusDto upsertBudget(User user, BudgetRequest request) {
        String category = request.category().trim();
        LocalDate monthStart = request.month().atDay(1);
        Budget budget = budgetRepository
                .findByUserIdAndCategoryIgnoreCaseAndBudgetMonth(user.getId(), category, monthStart)
                .orElseGet(Budget::new);
        budget.setUser(user);
        budget.setCategory(category);
        budget.setBudgetMonth(monthStart);
        budget.setLimitAmount(request.limitAmount());
        Budget saved = budgetRepository.save(budget);

        BigDecimal spent = spendingByCategory(user.getId(), request.month())
                .getOrDefault(category, BigDecimal.ZERO);
        return toStatus(saved, spent);
    }

    @Transactional
    public void deleteBudget(User user, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget " + budgetId + " was not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You cannot delete another user's budget");
        }
        budgetRepository.delete(budget);
    }

    private Map<String, BigDecimal> spendingByCategory(Long userId, YearMonth month) {
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
                userId, month.atDay(1), month.atEndOfMonth());
        Map<String, BigDecimal> spending = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        expenses.forEach(expense -> spending.merge(
                expense.getCategory(), expense.getAmount(), BigDecimal::add));
        return spending;
    }

    private BudgetStatusDto toStatus(Budget budget, BigDecimal spent) {
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        BigDecimal utilization = spent.multiply(BigDecimal.valueOf(100))
                .divide(budget.getLimitAmount(), 2, RoundingMode.HALF_UP);
        return new BudgetStatusDto(
                budget.getId(),
                budget.getCategory(),
                YearMonth.from(budget.getBudgetMonth()),
                budget.getLimitAmount(),
                spent,
                remaining,
                utilization,
                spent.compareTo(budget.getLimitAmount()) > 0
        );
    }
}
