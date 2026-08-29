package com.in6206.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    @Test
    void generatesAndValidatesTokenFromBase64Secret() {
        JwtUtils jwtUtils = new JwtUtils();
        byte[] key = new byte[64];
        for (int index = 0; index < key.length; index++) {
            key[index] = (byte) (index + 1);
        }
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", Base64.getEncoder().encodeToString(key));
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);

        String token = jwtUtils.generateJwtToken("example");

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("example");
    }
}
