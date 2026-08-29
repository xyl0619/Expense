package com.in6206.service;

import com.in6206.exception.ConflictException;
import com.in6206.model.Role;
import com.in6206.model.User;
import com.in6206.payload.SignupRequest;
import com.in6206.repository.RoleRepository;
import com.in6206.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    private SignupRequest signupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("example");
        request.setEmail("example@example.com");
        request.setPassword("strong-password");
        return request;
    }
}
