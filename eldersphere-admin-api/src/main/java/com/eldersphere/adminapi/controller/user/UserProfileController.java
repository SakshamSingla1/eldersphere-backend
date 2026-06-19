package com.eldersphere.adminapi.controller.user;

import com.eldersphere.adminapi.dto.user.request.UpdateUserProfileRequest;
import com.eldersphere.adminapi.dto.user.response.UserProfileResponse;
import com.eldersphere.adminapi.service.user.UserProfileService;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel<UserProfileResponse>> getProfile(@PathVariable Long userId) {
        return ApiResponse.successResponse(userProfileService.getProfileByUserId(userId));
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel<UserProfileResponse>> updateProfile(@PathVariable Long userId,
                                                                             @Valid @RequestBody UpdateUserProfileRequest request) {
        return ApiResponse.successResponse(userProfileService.updateProfile(userId, request), "Profile updated successfully");
    }
}
