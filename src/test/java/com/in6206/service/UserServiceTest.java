package com.in6206.service;

import com.in6206.exception.ConflictException;
import com.in6206.exception.ForbiddenOperationException;
import com.in6206.model.Role;
import com.in6206.model.User;
import com.in6206.payload.SignupRequest;
import com.in6206.repository.RoleRepository;
import com.in6206.repository.UserRepository;
import com.in6206.repository.BudgetRepository;
import com.in6206.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerHashesPasswordAndAssignsDefaultRole() {
        SignupRequest request = signupRequest();
        Role role = new Role();
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("strong-password")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertThat(saved.getPassword()).isEqualTo("bcrypt-hash");
        assertThat(saved.getRoles()).containsExactly(role);
    }

    @Test
    void registerRejectsDuplicateUsernameBeforeHashing() {
        SignupRequest request = signupRequest();
        when(userRepository.existsByUsername("example")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createsAdministratorWithHashedPasswordAndBothRoles() {
        Role userRole = role("ROLE_USER");
        Role adminRole = role("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("unique-admin-password")).thenReturn("bcrypt-admin-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.ensureAdministrator(
                "admin", "admin@example.com", "unique-admin-password");

        assertThat(saved.getPassword()).isEqualTo("bcrypt-admin-hash");
        assertThat(saved.getRoles()).containsExactlyInAnyOrder(userRole, adminRole);
    }

    @Test
    void keepsExistingAdministratorWithoutResettingPassword() {
        User existing = new User();
        existing.setUsername("admin");
        existing.setEmail("admin@example.com");
        existing.setPassword("existing-hash");
        existing.setRoles(Set.of(role("ROLE_ADMIN")));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existing));

        User result = userService.ensureAdministrator(
                "admin", "admin@example.com", "different-password");

        assertThat(result.getPassword()).isEqualTo("existing-hash");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void refusesToDeleteAdministrator() {
        User administrator = new User();
        administrator.setRoles(Set.of(role("ROLE_ADMIN")));
        when(userRepository.findById(7L)).thenReturn(Optional.of(administrator));

        assertThatThrownBy(() -> userService.deleteNonAdminUser(7L))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("Administrator");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deletesOrdinaryUserDataBeforeDeletingAccount() {
        User ordinaryUser = new User();
        ordinaryUser.setRoles(Set.of(role("ROLE_USER")));
        when(userRepository.findById(8L)).thenReturn(Optional.of(ordinaryUser));

        assertThat(userService.deleteNonAdminUser(8L)).isTrue();

        verify(budgetRepository).deleteAllByUserId(8L);
        verify(expenseRepository).deleteAllByUserId(8L);
        verify(userRepository).delete(ordinaryUser);
    }

    private SignupRequest signupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("example");
        request.setEmail("example@example.com");
        request.setPassword("strong-password");
        return request;
    }

    private Role role(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
