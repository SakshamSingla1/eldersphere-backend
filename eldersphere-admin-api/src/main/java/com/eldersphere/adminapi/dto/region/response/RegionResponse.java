package com.eldersphere.adminapi.dto.region.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RegionResponse {
    private Long id;
    private String name;
    private String state;
    private String country;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
