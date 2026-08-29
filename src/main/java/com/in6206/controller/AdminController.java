package com.in6206.controller;

import com.in6206.model.Expense;
import com.in6206.payload.AdminUserDto;
import com.in6206.payload.ExpenseDto;
import com.in6206.repository.ExpenseRepository;
import com.in6206.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public AdminController(UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        List<AdminUserDto> users = userRepository.findAll().stream()
                .map(AdminUserDto::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // templates/admin.html
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/expenses/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExpenseDto>> getGlobalReport() {
        List<Expense> all = expenseRepository.findAll();
        List<ExpenseDto> dtos = all.stream()
                .map(e -> new ExpenseDto(e.getId(), e.getAmount(), e.getCategory(),
                        e.getExpenseDate(), e.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
