package com.eldersphere.adminapi.dto.region.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegionRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
}
