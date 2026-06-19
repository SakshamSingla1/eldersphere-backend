package com.eldersphere.adminapi.dto.user.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String profilePhotoUrl;
    private String languagePreference;
    private Instant updatedAt;
}
