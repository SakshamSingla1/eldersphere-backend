package com.eldersphere.adminapi.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TwoFactorVerifyRequest {
    @NotBlank private String pendingToken;
    @NotBlank private String code;
}
