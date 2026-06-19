package com.eldersphere.adminapi.dto.address.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private Long userId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Instant createdAt;
    private Instant updatedAt;
}
