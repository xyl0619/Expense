package com.in6206.payload;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto {
    private Long id;
    private BigDecimal amount;
    private String category;
    private LocalDate expenseDate;
    private String description;

    // A constructor with parameters
    public ExpenseDto(Long id, BigDecimal amount, String category, LocalDate expenseDate, String description) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}