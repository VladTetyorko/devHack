//package com.vladte.devhack.service;
//
//import com.vladte.devhack.model.User;
//import com.vladte.devhack.repository.UserRepository;
//import com.vladte.devhack.service.domain.impl.UserServiceImpl;
//import com.vladte.devhack.util.AuditUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AuditUtil auditUtil;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private User testUser;
//    private User currentUser;
//    private List<User> testUsers;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // Create test user
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setName("Test User");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("password");
//
//        // Create current user (for audit operations)
//        currentUser = new User();
//        currentUser.setId(2L);
//        currentUser.setName("Current User");
//        currentUser.setEmail("current@example.com");
//        currentUser.setPassword("password");
//
//        testUsers = new ArrayList<>();
//        testUsers.add(testUser);
//    }
//
//    @Test
//    public void testSave() {
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        User savedUser = userService.save(testUser);
//
//        assertEquals(testUser, savedUser);
//        verify(userRepository, times(1)).save(testUser);
//    }
//
//    @Test
//    public void testFindAll() {
//        when(userRepository.findAll()).thenReturn(testUsers);
//
//        List<User> foundUsers = userService.findAll();
//
//        assertEquals(testUsers, foundUsers);
//        verify(userRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testFindById() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        Optional<User> foundUser = userService.findById(1L);
//
//        assertTrue(foundUser.isPresent());
//        assertEquals(testUser, foundUser.get());
//        verify(userRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    public void testDeleteById() {
//        doNothing().when(userRepository).deleteById(1L);
//
//        userService.deleteById(1L);
//
//        verify(userRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testSaveWithAudit() {
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        User savedUser = userService.saveWithAudit(testUser, currentUser, "Test details");
//
//        assertEquals(testUser, savedUser);
//        verify(userRepository, times(1)).save(testUser);
//        verify(auditUtil, times(1)).auditUpdate(testUser, currentUser, "Test details");
//    }
//
//    @Test
//    public void testSaveWithAudit_NewUser() {
//        // Set ID to null to simulate a new user
//        testUser.setId(null);
//
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User user = invocation.getArgument(0);
//            user.setId(1L); // Simulate ID generation
//            return user;
//        });
//
//        User savedUser = userService.saveWithAudit(testUser, currentUser, "Test details");
//
//        assertEquals(1L, savedUser.getId());
//        verify(userRepository, times(1)).save(testUser);
//        verify(auditUtil, times(1)).auditCreate(savedUser, currentUser, "Test details");
//        verify(auditUtil, never()).auditUpdate(any(), any(), any());
//    }
//
//    @Test
//    public void testFindByIdWithAudit() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        Optional<User> foundUser = userService.findByIdWithAudit(1L, currentUser, "Test details");
//
//        assertTrue(foundUser.isPresent());
//        assertEquals(testUser, foundUser.get());
//        verify(userRepository, times(1)).findById(1L);
//        verify(auditUtil, times(1)).auditRead(User.class, "1", currentUser, "Test details");
//    }
//
//    @Test
//    public void testDeleteByIdWithAudit() {
//        doNothing().when(userRepository).deleteById(1L);
//
//        userService.deleteByIdWithAudit(1L, currentUser, "Test details");
//
//        verify(userRepository, times(1)).deleteById(1L);
//        verify(auditUtil, times(1)).auditDelete(User.class, "1", currentUser, "Test details");
//    }
//
//    @Test
//    public void testGetSystemUser_Exists() {
//        // Create a system user
//        User systemUser = new User();
//        systemUser.setId(3L);
//        systemUser.setName("system");
//        systemUser.setEmail("system@devhack.com");
//        systemUser.setPassword("system");
//        systemUser.setRole("SYSTEM");
//
//        // Mock the repository to return the system user
//        when(userRepository.findByRole("SYSTEM")).thenReturn(Optional.of(systemUser));
//
//        // Call the method under test
//        User result = userService.getSystemUser();
//
//        // Verify the result
//        assertNotNull(result);
//        assertEquals(systemUser, result);
//        assertEquals("SYSTEM", result.getRole());
//
//        // Verify that the repository was called correctly
//        verify(userRepository, times(1)).findByRole("SYSTEM");
//
//        // Verify that save was not called (since the user already exists)
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testGetSystemUser_NotExists() {
//        // Mock the repository to return empty (no system user exists)
//        when(userRepository.findByRole("SYSTEM")).thenReturn(Optional.empty());
//
//        // Mock the save method to return a user with an ID
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User user = invocation.getArgument(0);
//            user.setId(3L); // Simulate ID generation
//            return user;
//        });
//
//        // Call the method under test
//        User result = userService.getSystemUser();
//
//        // Verify the result
//        assertNotNull(result);
//        assertEquals(3L, result.getId());
//        assertEquals("system", result.getName());
//        assertEquals("system@devhack.com", result.getEmail());
//        assertEquals("system", result.getPassword());
//        assertEquals("SYSTEM", result.getRole());
//
//        // Verify that the repository was called correctly
//        verify(userRepository, times(1)).findByRole("SYSTEM");
//
//        // Verify that save was called (since the user doesn't exist)
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//}
