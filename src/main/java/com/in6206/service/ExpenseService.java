package com.in6206.service;

import com.in6206.exception.ForbiddenOperationException;
import com.in6206.exception.InvalidRequestException;
import com.in6206.exception.ResourceNotFoundException;
import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.ExpenseDto;
import com.in6206.repository.ExpenseRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Expense createExpense(ExpenseDto request, User user) {
        Expense expense = new Expense();
        applyRequest(expense, request);
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, ExpenseDto request, User user) {
        Expense expense = requireOwnedExpense(id, user.getId());
        applyRequest(expense, request);
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id, User user) {
        Expense expense = requireOwnedExpense(id, user.getId());
        expenseRepository.delete(expense);
    }

    public Page<Expense> searchExpenses(Long userId,
                                        String category,
                                        LocalDate from,
                                        LocalDate to,
                                        BigDecimal minAmount,
                                        BigDecimal maxAmount,
                                        Pageable pageable) {
        validateRange(from, to, minAmount, maxAmount);
        return expenseRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), from));
            }
            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), to));
            }
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }

    private Expense requireOwnedExpense(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense " + expenseId + " was not found"));
        if (!expense.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("You cannot modify another user's expense");
        }
        return expense;
    }

    private void applyRequest(Expense expense, ExpenseDto request) {
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory().trim());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
    }

    private void validateRange(LocalDate from,
                               LocalDate to,
                               BigDecimal minAmount,
                               BigDecimal maxAmount) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidRequestException("The start date must not be after the end date");
        }
        if (minAmount != null && minAmount.signum() < 0) {
            throw new InvalidRequestException("Minimum amount must not be negative");
        }
        if (maxAmount != null && maxAmount.signum() < 0) {
            throw new InvalidRequestException("Maximum amount must not be negative");
        }
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new InvalidRequestException("Minimum amount must not exceed maximum amount");
        }
    }
}
