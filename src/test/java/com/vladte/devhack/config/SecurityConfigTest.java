package com.vladte.devhack.config;

import com.vladte.devhack.service.domain.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the SecurityConfig class.
 * These tests verify that the security configuration is working correctly.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that public resources are accessible without authentication.
     */
    @Test
    public void publicResourcesAreAccessible() throws Exception {
        // Test access to public static resources
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());

        // Test access to public pages
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    /**
     * Test that secured resources require authentication.
     */
    @Test
    public void securedResourcesRequireAuthentication() throws Exception {
        // Test access to secured pages
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Test that authenticated users can access secured resources.
     */
    @Test
    @WithMockUser
    public void authenticatedUsersCanAccessSecuredResources() throws Exception {
        // Test access to secured pages with authentication
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    /**
     * Test that the login form works correctly.
     */
    @Test
    public void loginFormWorks() throws Exception {
        // Test login form submission
        mockMvc.perform(post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "user@example.com")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that logout works correctly.
     */
    @Test
    @WithMockUser
    public void logoutWorks() throws Exception {
        // Test logout
        mockMvc.perform(post("/logout")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login?logout"));
    }

}
