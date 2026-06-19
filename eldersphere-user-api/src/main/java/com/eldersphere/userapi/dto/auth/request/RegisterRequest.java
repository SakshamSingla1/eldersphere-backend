package com.eldersphere.userapi.dto.auth.request;

import com.eldersphere.core.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    /**
     * Allowed roles for self-registration: ELDER, FAMILY_MEMBER, CARETAKER.
     * SUPER_ADMIN and ADMIN are rejected — use admin-api for those.
     */
    @NotNull(message = "Role is required")
    private UserRole role;
}
