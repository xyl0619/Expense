package com.in6206.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminPropertiesTest {

    @Test
    void acceptsCompleteSecureConfiguration() {
        AdminProperties properties = new AdminProperties(
                "admin", "admin@example.com", "unique-admin-password");

        assertThatCode(properties::validate).doesNotThrowAnyException();
    }

    @Test
    void rejectsPartialConfiguration() {
        AdminProperties properties = new AdminProperties("admin", "", "");

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must all be set");
    }

    @Test
    void rejectsTemplatePassword() {
        AdminProperties properties = new AdminProperties(
                "admin", "admin@example.com", "replace-with-a-unique-admin-password");

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("real password");
    }
}
