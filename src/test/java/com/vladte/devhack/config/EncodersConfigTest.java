package com.vladte.devhack.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the password encoder used in EncodersConfig.
 * These tests verify that the BCryptPasswordEncoder is working correctly.
 */
public class EncodersConfigTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Create the same encoder that is used in EncodersConfig
        passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Test that the password encoder can encode passwords.
     */
    @Test
    public void passwordEncoderCanEncodePasswords() {
        // Given
        String rawPassword = "testPassword123";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());
        // The encoded password should be different from the raw password
        assertNotEquals(rawPassword, encodedPassword);
        // BCrypt encoded passwords typically start with "$2a$" or "$2b$"
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }

    /**
     * Test that the password encoder can verify passwords.
     */
    @Test
    public void passwordEncoderCanVerifyPasswords() {
        // Given
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // When & Then
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    /**
     * Test that the password encoder generates different hashes for the same password.
     * This is important for security as it prevents rainbow table attacks.
     */
    @Test
    public void passwordEncoderGeneratesDifferentHashesForSamePassword() {
        // Given
        String rawPassword = "testPassword123";

        // When
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);

        // Then
        assertNotEquals(encodedPassword1, encodedPassword2);
    }

    @Test
    public void checkVladPassword() {
        // Given
        String rawPassword = "vlad";

        // When
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);
        System.out.println(passwordEncoder.encode(rawPassword));
        // Then
        assertNotEquals(encodedPassword1, encodedPassword2);
    }

}
