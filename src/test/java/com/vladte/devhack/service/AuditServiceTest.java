package com.vladte.devhack.service;

import com.vladte.devhack.model.Audit;
import com.vladte.devhack.model.User;
import com.vladte.devhack.repository.AuditRepository;
import com.vladte.devhack.service.domain.impl.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private Audit testAudit;
    private User testUser;
    private List<Audit> testAudits;
    private final UUID TEST_USER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID TEST_AUDIT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Create test audit
        testAudit = Audit.builder()
                .operationType(Audit.OperationType.CREATE)
                .entityType("TestEntity")
                .entityId(TEST_USER_ID.toString())
                .user(testUser)
                .timestamp(LocalDateTime.now())
                .details("Test details")
                .build();
        testAudit.setId(TEST_AUDIT_ID);

        testAudits = new ArrayList<>();
        testAudits.add(testAudit);
    }

    @Test
    public void testSave() {
        when(auditRepository.save(any(Audit.class))).thenReturn(testAudit);

        Audit savedAudit = auditService.save(testAudit);

        assertEquals(testAudit, savedAudit);
        verify(auditRepository, times(1)).save(testAudit);
    }

    @Test
    public void testFindAll() {
        when(auditRepository.findAll()).thenReturn(testAudits);

        List<Audit> foundAudits = auditService.findAll();

        assertEquals(testAudits, foundAudits);
        verify(auditRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(auditRepository.findById(TEST_AUDIT_ID)).thenReturn(Optional.of(testAudit));

        Optional<Audit> foundAudit = auditService.findById(TEST_AUDIT_ID);

        assertTrue(foundAudit.isPresent());
        assertEquals(testAudit, foundAudit.get());
        verify(auditRepository, times(1)).findById(TEST_AUDIT_ID);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(auditRepository).deleteById(TEST_AUDIT_ID);

        auditService.deleteById(TEST_AUDIT_ID);

        verify(auditRepository, times(1)).deleteById(TEST_AUDIT_ID);
    }

    @Test
    public void testAuditCreate() {
        when(auditRepository.save(any(Audit.class))).thenAnswer(invocation -> {
            Audit audit = invocation.getArgument(0);
            audit.setId(TEST_AUDIT_ID);
            return audit;
        });

        Audit audit = auditService.auditCreate("TestEntity", "1", testUser, "Test details");

        assertEquals(Audit.OperationType.CREATE, audit.getOperationType());
        assertEquals("TestEntity", audit.getEntityType());
        assertEquals("1", audit.getEntityId());
        assertEquals(testUser, audit.getUser());
        assertEquals("Test details", audit.getDetails());
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    public void testAuditRead() {
        when(auditRepository.save(any(Audit.class))).thenAnswer(invocation -> {
            Audit audit = invocation.getArgument(0);
            audit.setId(TEST_AUDIT_ID);
            return audit;
        });

        Audit audit = auditService.auditRead("TestEntity", "1", testUser, "Test details");

        assertEquals(Audit.OperationType.READ, audit.getOperationType());
        assertEquals("TestEntity", audit.getEntityType());
        assertEquals("1", audit.getEntityId());
        assertEquals(testUser, audit.getUser());
        assertEquals("Test details", audit.getDetails());
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    public void testAuditUpdate() {
        when(auditRepository.save(any(Audit.class))).thenAnswer(invocation -> {
            Audit audit = invocation.getArgument(0);
            audit.setId(TEST_AUDIT_ID);
            return audit;
        });

        Audit audit = auditService.auditUpdate("TestEntity", "1", testUser, "Test details");

        assertEquals(Audit.OperationType.UPDATE, audit.getOperationType());
        assertEquals("TestEntity", audit.getEntityType());
        assertEquals("1", audit.getEntityId());
        assertEquals(testUser, audit.getUser());
        assertEquals("Test details", audit.getDetails());
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    public void testAuditDelete() {
        when(auditRepository.save(any(Audit.class))).thenAnswer(invocation -> {
            Audit audit = invocation.getArgument(0);
            audit.setId(TEST_AUDIT_ID);
            return audit;
        });

        Audit audit = auditService.auditDelete("TestEntity", "1", testUser, "Test details");

        assertEquals(Audit.OperationType.DELETE, audit.getOperationType());
        assertEquals("TestEntity", audit.getEntityType());
        assertEquals("1", audit.getEntityId());
        assertEquals(testUser, audit.getUser());
        assertEquals("Test details", audit.getDetails());
        verify(auditRepository, times(1)).save(any(Audit.class));
    }
}
