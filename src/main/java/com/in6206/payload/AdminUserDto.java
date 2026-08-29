package com.in6206.payload;

import com.in6206.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public record AdminUserDto(Long id, String username, String email, Set<String> roles) {

    public static AdminUserDto from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toUnmodifiableSet());
        return new AdminUserDto(user.getId(), user.getUsername(), user.getEmail(), roleNames);
    }
}
