package com.eldersphere.adminapi.dto.auth.request;

import com.eldersphere.core.enums.SocialProvider;
import com.eldersphere.core.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SocialAuthRequest {

    /**
     * Firebase ID token obtained on the client after signing in with
     * Google / Facebook / Apple via Firebase SDK.
     */
    @NotBlank(message = "Firebase ID token is required")
    private String idToken;

    @NotNull(message = "Provider is required")
    private SocialProvider provider;

    /**
     * Required only when the user does not yet exist in the system.
     * Defaults to ELDER if omitted.
     */
    private UserRole role;
}
