package com.eldersphere.adminapi.dto.address.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressRequest {

    /** Required on create, ignored on update (scoped to path variable userId). */
    private Long userId;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 10)
    private String pincode;

    @Size(max = 100)
    private String country;

    private BigDecimal latitude;
    private BigDecimal longitude;
}
