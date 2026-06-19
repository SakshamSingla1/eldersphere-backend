package com.eldersphere.core.repository;

import com.eldersphere.core.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByActorId(Long actorId, Pageable pageable);

    Page<AuditLog> findAllByResourceTypeAndResourceId(String resourceType, Long resourceId, Pageable pageable);
}
