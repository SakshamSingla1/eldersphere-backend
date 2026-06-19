package com.eldersphere.adminapi.controller.admin;

import com.eldersphere.adminapi.dto.role.request.AssignPermissionsRequest;
import com.eldersphere.adminapi.dto.role.request.RoleRequest;
import com.eldersphere.adminapi.dto.role.response.RoleResponse;
import com.eldersphere.adminapi.service.admin.RoleService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ResponseModel<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.createSuccess(roleService.createRole(request), "Role created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<RoleResponse>> updateRole(@PathVariable Long id,
                                                                   @Valid @RequestBody RoleRequest request) {
        return ApiResponse.successResponse(roleService.updateRole(id, request), "Role updated successfully");
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<ResponseModel<RoleResponse>> assignPermissions(@PathVariable Long id,
                                                                          @Valid @RequestBody AssignPermissionsRequest request) {
        return ApiResponse.successResponse(roleService.assignPermissions(id, request), "Permissions assigned successfully");
    }

    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<ResponseModel<RoleResponse>> removePermissions(@PathVariable Long id,
                                                                          @Valid @RequestBody AssignPermissionsRequest request) {
        return ApiResponse.successResponse(roleService.removePermissions(id, request), "Permissions removed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.successResponse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RoleResponse>> getRoleById(@PathVariable Long id) {
        return ApiResponse.successResponse(roleService.getRoleById(id));
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<RoleResponse>>> getAllRoles() {
        return ApiResponse.successResponse(roleService.getAllRoles());
    }
}
