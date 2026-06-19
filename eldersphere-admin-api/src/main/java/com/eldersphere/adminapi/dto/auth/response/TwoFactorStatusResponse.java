package com.eldersphere.adminapi.dto.auth.response;

import com.eldersphere.core.enums.TwoFactorMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorStatusResponse {
    private boolean enabled;
    private TwoFactorMethod method;
}
