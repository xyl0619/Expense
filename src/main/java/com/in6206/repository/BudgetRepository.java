package com.in6206.repository;

import com.in6206.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndBudgetMonthOrderByCategoryAsc(Long userId, LocalDate budgetMonth);

    Optional<Budget> findByUserIdAndCategoryIgnoreCaseAndBudgetMonth(
            Long userId, String category, LocalDate budgetMonth);

    @Modifying(flushAutomatically = true)
    @Query("delete from Budget budget where budget.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
