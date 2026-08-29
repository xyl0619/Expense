package com.in6206.payload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.in6206.model.Role;
import com.in6206.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AdminUserDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializedDtoDoesNotExposePassword() throws Exception {
        Role role = new Role();
        role.setName("ROLE_USER");
        User user = new User();
        user.setId(1L);
        user.setUsername("example");
        user.setEmail("example@example.com");
        user.setPassword("sensitive-hash");
        user.setRoles(Set.of(role));

        String json = objectMapper.writeValueAsString(AdminUserDto.from(user));

        assertThat(json)
                .contains("ROLE_USER")
                .doesNotContain("password", "sensitive-hash");
    }
}
