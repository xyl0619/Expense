package com.in6206.controller;

import com.in6206.payload.AdminUserDto;
import com.in6206.payload.AdminExpenseDto;
import com.in6206.repository.ExpenseRepository;
import com.in6206.repository.UserRepository;
import com.in6206.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public AdminController(UserRepository userRepository,
                           ExpenseRepository expenseRepository,
                           UserService userService) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        List<AdminUserDto> users = userRepository.findAll().stream()
                .map(AdminUserDto::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteNonAdminUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/expenses/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminExpenseDto>> getGlobalReport() {
        return ResponseEntity.ok(expenseRepository.findAllForAdminReport());
    }
}
