package com.vladte.devhack.controller;

import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the AuthController class.
 */
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Configure view resolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setViewResolvers(viewResolver)
                .build();

        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("existing@example.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
    }

    /**
     * Test that the login page is displayed correctly.
     */
    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    /**
     * Test that the registration page is displayed correctly.
     */
    @Test
    public void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    /**
     * Test that the registration form submission works correctly when the input is valid.
     */
    @Test
    public void testRegisterSubmit_ValidInput() throws Exception {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/register")
                .param("name", "New User")
                .param("email", "new@example.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        // Verify
        verify(userService, times(1)).findByEmail("new@example.com");
        verify(userService, times(1)).reguister(any(User.class));
    }

    /**
     * Test that the registration form submission shows errors when the email already exists.
     */
    @Test
    public void testRegisterSubmit_EmailExists() throws Exception {
        // Arrange
        when(userService.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = authController.registerSubmit(testUser, bindingResult, model);

        // Assert
        assertEquals("auth/register", viewName);

        // Verify
        verify(userService, times(1)).findByEmail("existing@example.com");
        verify(bindingResult, times(1)).rejectValue(eq("email"), eq("error.user"), anyString());
        verify(userService, never()).save(any(User.class));
    }

    /**
     * Test that the dashboard page is displayed correctly.
     */
    @Test
    public void testDashboardPage() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}
