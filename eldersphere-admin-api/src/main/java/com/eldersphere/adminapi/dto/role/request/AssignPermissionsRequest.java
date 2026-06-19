package com.eldersphere.adminapi.dto.role.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class AssignPermissionsRequest {

    @NotEmpty(message = "Permission IDs must not be empty")
    private Set<Long> permissionIds;
}
