package com.eldersphere.adminapi.service.admin;

import com.eldersphere.adminapi.dto.permission.request.CreatePermissionRequest;
import com.eldersphere.adminapi.dto.permission.response.PermissionResponse;
import com.eldersphere.core.dao.admin.PermissionDao;
import com.eldersphere.core.entities.Permission;
import com.eldersphere.core.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final PermissionDao permissionDao;

    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionDao.existsByResourceAndAction(request.getResource(), request.getAction())) {
            throw BadRequestException.conflict("Permission already exists for " + request.getResource() + ":" + request.getAction());
        }
        Permission permission = Permission.builder()
                .resource(request.getResource())
                .action(request.getAction())
                .description(request.getDescription())
                .build();
        return toResponse(permissionDao.save(permission));
    }

    public PermissionResponse getPermissionById(Long id) {
        return toResponse(permissionDao.findByIdOrThrow(id));
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionDao.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PermissionResponse> getPermissionsByResource(String resource) {
        return permissionDao.findAllByResource(resource).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deletePermission(Long id) {
        permissionDao.findByIdOrThrow(id);
        permissionDao.delete(id);
    }

    public PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}
