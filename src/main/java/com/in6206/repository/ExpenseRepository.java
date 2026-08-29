package com.in6206.repository;

import com.in6206.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
}
