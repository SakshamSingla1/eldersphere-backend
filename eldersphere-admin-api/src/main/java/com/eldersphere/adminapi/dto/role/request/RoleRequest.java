package com.eldersphere.adminapi.dto.role.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {

    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    private String description;

    /** Only used on create — ignored on update (use assign/remove permission endpoints instead). */
    private Set<Long> permissionIds;
}
