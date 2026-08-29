package com.in6206.controller;

import com.in6206.model.User;
import com.in6206.payload.BudgetRequest;
import com.in6206.payload.BudgetStatusDto;
import com.in6206.payload.MonthlyBudgetOverviewDto;
import com.in6206.service.BudgetService;
import com.in6206.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "Monthly category budget management")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;
    private final CurrentUserService currentUserService;

    public BudgetController(BudgetService budgetService, CurrentUserService currentUserService) {
        this.budgetService = budgetService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "Get budget utilization for a month")
    public MonthlyBudgetOverviewDto getMonthlyOverview(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        User user = currentUserService.requireCurrentUser();
        return budgetService.getMonthlyOverview(user, month == null ? YearMonth.now() : month);
    }

    @PostMapping
    @Operation(summary = "Create or update a category budget")
    public BudgetStatusDto upsertBudget(@Valid @RequestBody BudgetRequest request) {
        User user = currentUserService.requireCurrentUser();
        return budgetService.upsertBudget(user, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget owned by the current user")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        budgetService.deleteBudget(user, id);
        return ResponseEntity.noContent().build();
    }
}
