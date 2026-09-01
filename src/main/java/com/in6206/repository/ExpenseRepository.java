package com.in6206.repository;

import com.in6206.model.Expense;
import com.in6206.payload.AdminExpenseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByUserId(Long userId);

    @Query("""
            select new com.in6206.payload.AdminExpenseDto(
                expense.id,
                expense.user.username,
                expense.amount,
                expense.category,
                expense.expenseDate,
                expense.description
            )
            from Expense expense
            order by expense.expenseDate desc, expense.id desc
            """)
    List<AdminExpenseDto> findAllForAdminReport();

    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Modifying(flushAutomatically = true)
    @Query("delete from Expense expense where expense.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
