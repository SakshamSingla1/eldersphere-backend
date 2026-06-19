package com.eldersphere.adminapi.dto.auth.response;

import com.eldersphere.core.enums.TwoFactorMethod;
import com.eldersphere.core.enums.UserRole;
import com.eldersphere.core.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private Boolean requiresTwoFactor;
    private String pendingToken;
    private TwoFactorMethod twoFactorMethod;
}
