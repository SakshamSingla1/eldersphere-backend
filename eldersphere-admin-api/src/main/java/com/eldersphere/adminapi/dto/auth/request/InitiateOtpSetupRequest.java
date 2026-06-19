package com.eldersphere.adminapi.dto.auth.request;

import com.eldersphere.core.enums.TwoFactorMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiateOtpSetupRequest {
    @NotNull private TwoFactorMethod method; // SMS or EMAIL only
}
