package com.vladte.devhack.service;

import com.vladte.devhack.model.User;
import com.vladte.devhack.repository.UserRepository;
import com.vladte.devhack.service.domain.AuditService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.domain.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the security-related functionality of UserService.
 */
public class UserServiceSecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService self;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");

        // Mock the self reference for getSystemUser
        when(self.getSystemUser()).thenReturn(testUser);
    }

    /**
     * Test that loadUserByUsername returns a valid UserDetails object when the user exists.
     */
    @Test
    public void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        // Verify
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    /**
     * Test that loadUserByUsername throws UsernameNotFoundException when the user doesn't exist.
     */
    @Test
    public void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    /**
     * Test that register method encodes the password and sets the role.
     */
    @Test
    public void register_EncodesPasswordAndSetsRole() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID()); // Set an ID for the saved user
            return user;
        });

        // Act
        userService.reguister(newUser);

        // Assert
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals("USER", newUser.getRole());

        // Verify
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(newUser);
    }
}
