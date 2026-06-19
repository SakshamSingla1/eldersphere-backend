package com.eldersphere.adminapi.dto.user.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {

    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    private String profilePhotoUrl;

    @Size(max = 20, message = "Language preference must not exceed 20 characters")
    private String languagePreference;
}
