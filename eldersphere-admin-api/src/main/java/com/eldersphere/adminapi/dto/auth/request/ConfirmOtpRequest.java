package com.eldersphere.adminapi.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmOtpRequest {
    @NotBlank private String code;
}
