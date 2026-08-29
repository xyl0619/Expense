package com.in6206.service;

import com.in6206.model.User;
import com.in6206.repository.UserRepository;
import com.in6206.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireCurrentUserLoadsDomainUserFromAuthenticatedPrincipal() {
        User user = new User();
        user.setId(1L);
        UserDetailsImpl principal = new UserDetailsImpl(1L, "example", "example@example.com", "hash", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(currentUserService.requireCurrentUser()).isSameAs(user);
    }

    @Test
    void requireCurrentUserRejectsAnonymousRequest() {
        assertThatThrownBy(() -> currentUserService.requireCurrentUser())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
