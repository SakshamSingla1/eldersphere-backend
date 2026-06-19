package com.eldersphere.adminapi.service.admin;

import com.eldersphere.core.dao.admin.AuditLogDao;
import com.eldersphere.core.entities.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogDao auditLogDao;

    public Page<AuditLog> getLogsByActor(Long actorId, Pageable pageable) {
        return auditLogDao.findByActorId(actorId, pageable);
    }

    public Page<AuditLog> getLogsByResource(String resourceType, Long resourceId, Pageable pageable) {
        return auditLogDao.findByResource(resourceType, resourceId, pageable);
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogDao.findAll(pageable);
    }
}
