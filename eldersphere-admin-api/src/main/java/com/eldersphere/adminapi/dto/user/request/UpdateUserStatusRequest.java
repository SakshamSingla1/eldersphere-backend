package com.eldersphere.adminapi.dto.user.request;

import com.eldersphere.core.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;

    private String reason;
}
