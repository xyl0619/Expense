package com.in6206.repository;

import com.in6206.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndBudgetMonthOrderByCategoryAsc(Long userId, LocalDate budgetMonth);

    Optional<Budget> findByUserIdAndCategoryIgnoreCaseAndBudgetMonth(
            Long userId, String category, LocalDate budgetMonth);
}
