package com.in6206.controller;

import com.in6206.model.Expense;
import com.in6206.model.User;
import com.in6206.payload.ExpenseDto;
import com.in6206.repository.UserRepository;
import com.in6206.security.UserDetailsImpl;
import com.in6206.service.ExpenseService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        User currentUser = getCurrentUser();
       // User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Expense> expenses = expenseService.getAllExpensesByUser(currentUser.getId());
        List<ExpenseDto> dtos = expenses.stream()
                .map(e -> new ExpenseDto(e.getId(), e.getAmount(), e.getCategory(), e.getExpenseDate(), e.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@RequestBody ExpenseDto expenseDto) {
        User currentUser = getCurrentUser();
        //User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense expense = new Expense();
        expense.setAmount(expenseDto.getAmount());
        expense.setCategory(expenseDto.getCategory());
        expense.setExpenseDate(expenseDto.getExpenseDate());
        expense.setDescription(expenseDto.getDescription());
        Expense saved = expenseService.createExpense(expense, currentUser);
        ExpenseDto dto = new ExpenseDto(saved.getId(), saved.getAmount(), saved.getCategory(), saved.getExpenseDate(), saved.getDescription());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Expense existing = expenseService.getExpenseById(id);
        if (!existing.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id, @RequestBody ExpenseDto expenseDto) {
        User currentUser = getCurrentUser();
       // User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense existing = expenseService.getExpenseById(id);
        if (!existing.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        existing.setAmount(expenseDto.getAmount());
        existing.setCategory(expenseDto.getCategory());
        existing.setExpenseDate(expenseDto.getExpenseDate());
        existing.setDescription(expenseDto.getDescription());
        Expense updated = expenseService.saveExpense(existing);
        ExpenseDto dto = new ExpenseDto(updated.getId(), updated.getAmount(), updated.getCategory(),
                updated.getExpenseDate(), updated.getDescription());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        User currentUser = getCurrentUser();
       // User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Expense> expenses = expenseService.getAllExpensesByUser(currentUser.getId());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Amount,Category,Date,Description");
        for (Expense e : expenses) {
            writer.printf("%d,%.2f,%s,%s,%s%n",
                    e.getId(), e.getAmount(), e.getCategory(),
                    e.getExpenseDate(), e.getDescription() == null ? "" : e.getDescription());
        }
        writer.flush();
    }
}
