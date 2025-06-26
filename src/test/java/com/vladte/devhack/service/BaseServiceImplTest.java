package com.vladte.devhack.service;

import com.vladte.devhack.model.BasicEntity;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.AuditService;
import com.vladte.devhack.service.domain.impl.BaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BaseServiceImplTest {

    // Test entity class
    private static class TestEntity extends BasicEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Test repository interface
    private interface TestRepository extends JpaRepository<TestEntity, UUID> {
    }

    // Concrete implementation of BaseServiceImpl for testing
    private static class TestServiceImpl extends BaseServiceImpl<TestEntity, UUID, TestRepository> {
        public TestServiceImpl(TestRepository repository, AuditService auditService) {
            super(repository, auditService);
        }

        public TestServiceImpl(TestRepository repository) {
            super(repository);
        }
    }

    @Mock
    private TestRepository repository;

    @Mock
    private AuditService auditService;

    private TestServiceImpl serviceWithAudit;
    private TestServiceImpl serviceWithoutAudit;
    private TestEntity testEntity;
    private User testUser;
    private final UUID TEST_ENTITY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        serviceWithAudit = new TestServiceImpl(repository, auditService);
        serviceWithoutAudit = new TestServiceImpl(repository);

        // Create test entity
        testEntity = new TestEntity();
        testEntity.setId(TEST_ENTITY_ID);
        testEntity.setName("Test Entity");
        testEntity.setCreatedAt(LocalDateTime.now());

        // Create test user
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
    }

    @Test
    public void testSave() {
        when(repository.save(any(TestEntity.class))).thenReturn(testEntity);

        TestEntity savedEntity = serviceWithoutAudit.save(testEntity);

        assertEquals(testEntity, savedEntity);
        verify(repository, times(1)).save(testEntity);
    }

    @Test
    public void testSaveWithAudit_NewEntity() {
        // Set ID to null to simulate a new entity
        testEntity.setId(null);

        when(repository.save(any(TestEntity.class))).thenAnswer(invocation -> {
            TestEntity entity = invocation.getArgument(0);
            entity.setId(TEST_ENTITY_ID); // Simulate ID generation
            return entity;
        });

        TestEntity savedEntity = serviceWithAudit.save(testEntity, testUser, "Test details");

        assertEquals(1L, savedEntity.getId());
        verify(repository, times(1)).save(testEntity);
        verify(auditService, times(1)).auditCreate(eq("TestEntity"), eq("1"), eq(testUser), eq("Test details"));
        verify(auditService, never()).auditUpdate(any(), any(), any(), any());
    }

    @Test
    public void testSaveWithAudit_ExistingEntity() {
        when(repository.save(any(TestEntity.class))).thenReturn(testEntity);

        TestEntity savedEntity = serviceWithAudit.save(testEntity, testUser, "Test details");

        assertEquals(testEntity, savedEntity);
        verify(repository, times(1)).save(testEntity);
        verify(auditService, never()).auditCreate(any(), any(), any(), any());
        verify(auditService, times(1)).auditUpdate(eq("TestEntity"), eq("1"), eq(testUser), eq("Test details"));
    }

    @Test
    public void testFindAll() {
        List<TestEntity> entities = new ArrayList<>();
        entities.add(testEntity);

        when(repository.findAll()).thenReturn(entities);

        List<TestEntity> foundEntities = serviceWithoutAudit.findAll();

        assertEquals(1, foundEntities.size());
        assertEquals(testEntity, foundEntities.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(repository.findById(TEST_ENTITY_ID)).thenReturn(Optional.of(testEntity));

        Optional<TestEntity> foundEntity = serviceWithoutAudit.findById(TEST_ENTITY_ID);

        assertTrue(foundEntity.isPresent());
        assertEquals(testEntity, foundEntity.get());
        verify(repository, times(1)).findById(TEST_ENTITY_ID);
    }

    @Test
    public void testFindByIdWithAudit() {
        when(repository.findById(TEST_ENTITY_ID)).thenReturn(Optional.of(testEntity));

        Optional<TestEntity> foundEntity = serviceWithAudit.findById(TEST_ENTITY_ID, testUser, "Test details");

        assertTrue(foundEntity.isPresent());
        assertEquals(testEntity, foundEntity.get());
        verify(repository, times(1)).findById(TEST_ENTITY_ID);
        verify(auditService, times(1)).auditRead(eq("TestEntity"), eq(TEST_ENTITY_ID.toString()), eq(testUser), eq("Test details"));
    }

    @Test
    public void testDeleteById() {
        doNothing().when(repository).deleteById(TEST_ENTITY_ID);

        serviceWithoutAudit.deleteById(TEST_ENTITY_ID);

        verify(repository, times(1)).deleteById(TEST_ENTITY_ID);
    }

    @Test
    public void testDeleteByIdWithAudit() {
        doNothing().when(repository).deleteById(TEST_ENTITY_ID);

        serviceWithAudit.deleteById(TEST_ENTITY_ID, TestEntity.class, testUser, "Test details");

        verify(repository, times(1)).deleteById(TEST_ENTITY_ID);
        verify(auditService, times(1)).auditDelete(eq("TestEntity"), eq(TEST_ENTITY_ID.toString()), eq(testUser), eq("Test details"));
    }
}
