package com.eldersphere.userapi.dto.auth.request;

import com.eldersphere.core.enums.SocialProvider;
import com.eldersphere.core.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SocialAuthRequest {

    @NotBlank(message = "Firebase ID token is required")
    private String idToken;

    @NotNull(message = "Provider is required")
    private SocialProvider provider;

    /**
     * Required only for new user registration via OAuth.
     * Allowed: ELDER, FAMILY_MEMBER, CARETAKER. Defaults to ELDER if omitted.
     */
    private UserRole role;
}
