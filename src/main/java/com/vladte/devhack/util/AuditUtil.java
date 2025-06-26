package com.vladte.devhack.util;

import com.vladte.devhack.model.BasicEntity;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.AuditService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Utility class for creating audit records.
 * This class provides a convenient way for services to create audit records.
 */
@Component
public class AuditUtil {

    private final AuditService auditService;

    /**
     * Constructor with AuditService injection.
     *
     * @param auditService the audit service
     */
    public AuditUtil(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Create an audit record for a create operation asynchronously.
     *
     * @param entity the entity being created
     * @param user the user performing the operation
     * @param details additional details about the operation
     */
    @Async
    public void auditCreate(BasicEntity entity, User user, String details) {
        String entityType = entity.getClass().getSimpleName();
        String entityId = entity.getId() != null ? entity.getId().toString() : null;
        auditService.auditCreate(entityType, entityId, user, details);
    }

    /**
     * Create an audit record for a read operation asynchronously.
     *
     * @param entityClass the class of the entity being read
     * @param entityId the ID of the entity being read
     * @param user the user performing the operation
     * @param details additional details about the operation
     */
    @Async
    public void auditRead(Class<?> entityClass, String entityId, User user, String details) {
        String entityType = entityClass.getSimpleName();
        auditService.auditRead(entityType, entityId, user, details);
    }

    /**
     * Create an audit record for an update operation asynchronously.
     *
     * @param entity the entity being updated
     * @param user the user performing the operation
     * @param details additional details about the operation
     */
    @Async
    public void auditUpdate(BasicEntity entity, User user, String details) {
        String entityType = entity.getClass().getSimpleName();
        String entityId = entity.getId() != null ? entity.getId().toString() : null;
        auditService.auditUpdate(entityType, entityId, user, details);
    }

    /**
     * Create an audit record for a delete operation asynchronously.
     *
     * @param entityClass the class of the entity being deleted
     * @param entityId the ID of the entity being deleted
     * @param user the user performing the operation
     * @param details additional details about the operation
     */
    @Async
    public void auditDelete(Class<?> entityClass, String entityId, User user, String details) {
        String entityType = entityClass.getSimpleName();
        auditService.auditDelete(entityType, entityId, user, details);
    }

}
