package com.eldersphere.adminapi.dto.user.response;

import com.eldersphere.core.enums.UserRole;
import com.eldersphere.core.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private Long regionId;
    private String regionName;
    private String fullName;
    private String profilePhotoUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
