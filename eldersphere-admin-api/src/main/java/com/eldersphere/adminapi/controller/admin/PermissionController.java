package com.eldersphere.adminapi.controller.admin;

import com.eldersphere.adminapi.dto.permission.request.CreatePermissionRequest;
import com.eldersphere.adminapi.dto.permission.response.PermissionResponse;
import com.eldersphere.adminapi.service.admin.PermissionService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ResponseModel<PermissionResponse>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ApiResponse.createSuccess(permissionService.createPermission(request), "Permission created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<PermissionResponse>> getPermissionById(@PathVariable Long id) {
        return ApiResponse.successResponse(permissionService.getPermissionById(id));
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<PermissionResponse>>> getAllPermissions() {
        return ApiResponse.successResponse(permissionService.getAllPermissions());
    }

    @GetMapping("/resource/{resource}")
    public ResponseEntity<ResponseModel<List<PermissionResponse>>> getPermissionsByResource(@PathVariable String resource) {
        return ApiResponse.successResponse(permissionService.getPermissionsByResource(resource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.successResponse();
    }
}
