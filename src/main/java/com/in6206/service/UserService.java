package com.in6206.service;

import com.in6206.model.User;
import com.in6206.model.Role;
import com.in6206.exception.ConflictException;
import com.in6206.exception.ForbiddenOperationException;
import com.in6206.payload.SignupRequest;
import com.in6206.repository.RoleRepository;
import com.in6206.repository.UserRepository;
import com.in6206.repository.BudgetRepository;
import com.in6206.repository.ExpenseRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       BudgetRepository budgetRepository,
                       ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new ConflictException("Username is already registered");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        user.setRoles(new HashSet<>(Set.of(getOrCreateRole("ROLE_USER"))));

        return userRepository.save(user);
    }

    @Transactional
    public User ensureAdministrator(String username, String email, String password) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            User user = existing.get();
            if (!user.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalStateException(
                        "ADMIN_USERNAME already belongs to a different email address");
            }
            if (hasRole(user, "ROLE_ADMIN")) {
                return user;
            }

            user.setPassword(passwordEncoder.encode(password));
            user.getRoles().add(getOrCreateRole("ROLE_USER"));
            user.getRoles().add(getOrCreateRole("ROLE_ADMIN"));
            return userRepository.save(user);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("ADMIN_EMAIL already belongs to a different username");
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(new HashSet<>(Set.of(
                getOrCreateRole("ROLE_USER"),
                getOrCreateRole("ROLE_ADMIN")
        )));
        return userRepository.save(admin);
    }

    @Transactional
    public boolean deleteNonAdminUser(Long id) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        if (hasRole(existing.get(), "ROLE_ADMIN")) {
            throw new ForbiddenOperationException("Administrator accounts cannot be deleted here");
        }
        budgetRepository.deleteAllByUserId(id);
        expenseRepository.deleteAllByUserId(id);
        userRepository.delete(existing.get());
        return true;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private Role getOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(role -> roleName.equals(role.getName()));
    }
}
