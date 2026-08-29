package com.in6206.controller;

import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.ExpenseDto;
import com.in6206.service.CurrentUserService;
import com.in6206.service.ExpenseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CurrentUserService currentUserService;

    public ExpenseController(ExpenseService expenseService, CurrentUserService currentUserService) {
        this.expenseService = expenseService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        User currentUser = currentUserService.requireCurrentUser();
        List<Expense> expenses = expenseService.getAllExpensesByUser(currentUser.getId());
        List<ExpenseDto> dtos = expenses.stream().map(ExpenseDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public Page<ExpenseDto> searchExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @PageableDefault(size = 10, sort = "expenseDate", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        User currentUser = currentUserService.requireCurrentUser();
        return expenseService.searchExpenses(
                        currentUser.getId(), category, from, to, minAmount, maxAmount, pageable)
                .map(ExpenseDto::from);
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        User currentUser = currentUserService.requireCurrentUser();
        Expense saved = expenseService.createExpense(expenseDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseDto.from(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        User currentUser = currentUserService.requireCurrentUser();
        expenseService.deleteExpense(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id,
                                                     @Valid @RequestBody ExpenseDto expenseDto) {
        User currentUser = currentUserService.requireCurrentUser();
        Expense updated = expenseService.updateExpense(id, expenseDto, currentUser);
        return ResponseEntity.ok(ExpenseDto.from(updated));
    }

    @GetMapping("/report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        User currentUser = currentUserService.requireCurrentUser();
        List<Expense> expenses = expenseService.getAllExpensesByUser(currentUser.getId());

        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');
        writer.println("ID,Amount,Category,Date,Description");
        for (Expense e : expenses) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    e.getId(),
                    e.getAmount().toPlainString(),
                    csvCell(e.getCategory()),
                    e.getExpenseDate(),
                    csvCell(e.getDescription()));
        }
        writer.flush();
    }

    private String csvCell(String value) {
        String safe = value == null ? "" : value;
        if (!safe.isEmpty() && "=+-@".indexOf(safe.charAt(0)) >= 0) {
            safe = "'" + safe;
        }
        return '"' + safe.replace("\"", "\"\"") + '"';
    }
}
