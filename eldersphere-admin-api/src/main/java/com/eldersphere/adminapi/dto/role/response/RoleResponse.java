package com.eldersphere.adminapi.dto.role.response;

import com.eldersphere.adminapi.dto.permission.response.PermissionResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
    private Instant createdAt;
    private Instant updatedAt;
}
