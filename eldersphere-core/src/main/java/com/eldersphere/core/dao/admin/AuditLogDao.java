package com.eldersphere.core.dao.admin;

import com.eldersphere.core.entities.AuditLog;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AuditLogDao {

    private final AuditLogRepository auditLogRepository;

    public AuditLogDao(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public AuditLog save(AuditLog auditLog) throws ElderSphereException {
        try {
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save audit log");
        }
    }

    public Page<AuditLog> findByActorId(Long actorId, Pageable pageable) {
        return auditLogRepository.findAllByActorId(actorId, pageable);
    }

    public Page<AuditLog> findByResource(String resourceType, Long resourceId, Pageable pageable) {
        return auditLogRepository.findAllByResourceTypeAndResourceId(resourceType, resourceId, pageable);
    }

    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
