package com.eldersphere.adminapi.dto.permission.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PermissionResponse {
    private Long id;
    private String resource;
    private String action;
    private String description;
    private Instant createdAt;
}
