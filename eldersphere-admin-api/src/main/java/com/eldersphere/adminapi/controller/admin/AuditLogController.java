package com.eldersphere.adminapi.controller.admin;

import com.eldersphere.adminapi.service.admin.AuditLogService;
import com.eldersphere.core.entities.AuditLog;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ResponseModel<Page<AuditLog>>> getLogs(
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Long resourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (resourceType != null && resourceId != null) {
            return ApiResponse.successResponse(auditLogService.getLogsByResource(resourceType, resourceId, pageable));
        }
        if (actorId != null) {
            return ApiResponse.successResponse(auditLogService.getLogsByActor(actorId, pageable));
        }
        return ApiResponse.successResponse(auditLogService.getAllLogs(pageable));
    }
}
