package com.in6206.service;

import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Expense createExpense(Expense expense, User user) {
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
}