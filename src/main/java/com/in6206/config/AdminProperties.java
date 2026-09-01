package com.in6206.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(String username, String email, String password) {

    public AdminProperties {
        username = normalize(username);
        email = normalize(email);
        password = password == null ? "" : password;
    }

    public boolean isEmpty() {
        return username.isEmpty() && email.isEmpty() && password.isEmpty();
    }

    public void validate() {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException(
                    "ADMIN_USERNAME, ADMIN_EMAIL and ADMIN_PASSWORD must all be set");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalStateException("ADMIN_USERNAME must contain 3 to 20 characters");
        }
        if (email.length() > 50 || !email.contains("@")) {
            throw new IllegalStateException("ADMIN_EMAIL must be a valid email address up to 50 characters");
        }
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (passwordBytes < 12 || passwordBytes > 72 || password.startsWith("replace-with-")) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD must be a real password containing 12 to 72 UTF-8 bytes");
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
