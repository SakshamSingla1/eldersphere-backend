package com.eldersphere.adminapi.service.admin;

import com.eldersphere.adminapi.dto.permission.response.PermissionResponse;
import com.eldersphere.adminapi.dto.role.request.AssignPermissionsRequest;
import com.eldersphere.adminapi.dto.role.request.RoleRequest;
import com.eldersphere.adminapi.dto.role.response.RoleResponse;
import com.eldersphere.core.dao.admin.PermissionDao;
import com.eldersphere.core.dao.admin.RoleDao;
import com.eldersphere.core.entities.Permission;
import com.eldersphere.core.entities.Role;
import com.eldersphere.core.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleDao roleDao;
    private final PermissionDao permissionDao;

    public RoleResponse createRole(RoleRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw BadRequestException.badRequest("Role name is required");
        }
        if (roleDao.existsByName(request.getName())) {
            throw BadRequestException.conflict("Role already exists with name: " + request.getName());
        }
        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionIds() != null) {
            for (Long permId : request.getPermissionIds()) {
                permissions.add(permissionDao.findByIdOrThrow(permId));
            }
        }
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();
        return toResponse(roleDao.save(role));
    }

    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleDao.findByIdOrThrow(id);
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            if (roleDao.existsByName(request.getName())) {
                throw BadRequestException.conflict("Role already exists with name: " + request.getName());
            }
            role.setName(request.getName());
        }
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        return toResponse(roleDao.update(role));
    }

    public RoleResponse assignPermissions(Long roleId, AssignPermissionsRequest request) {
        Role role = roleDao.findByIdOrThrow(roleId);
        for (Long permId : request.getPermissionIds()) {
            role.getPermissions().add(permissionDao.findByIdOrThrow(permId));
        }
        return toResponse(roleDao.update(role));
    }

    public RoleResponse removePermissions(Long roleId, AssignPermissionsRequest request) {
        Role role = roleDao.findByIdOrThrow(roleId);
        role.getPermissions().removeIf(p -> request.getPermissionIds().contains(p.getId()));
        return toResponse(roleDao.update(role));
    }

    public void deleteRole(Long id) {
        roleDao.findByIdOrThrow(id);
        roleDao.delete(id);
    }

    public RoleResponse getRoleById(Long id) {
        return toResponse(roleDao.findByIdOrThrow(id));
    }

    public List<RoleResponse> getAllRoles() {
        return roleDao.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RoleResponse toResponse(Role role) {
        Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(p -> PermissionResponse.builder()
                        .id(p.getId())
                        .resource(p.getResource())
                        .action(p.getAction())
                        .description(p.getDescription())
                        .createdAt(p.getCreatedAt())
                        .build())
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionResponses)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
