package com.eldersphere.adminapi.service.user;

import com.eldersphere.adminapi.dto.user.request.UpdateUserProfileRequest;
import com.eldersphere.adminapi.dto.user.response.UserProfileResponse;
import com.eldersphere.core.dao.auth.UserProfileDao;
import com.eldersphere.core.entities.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileDao userProfileDao;

    public UserProfileResponse getProfileByUserId(Long userId) {
        return toResponse(userProfileDao.findByUserIdOrThrow(userId));
    }

    public UserProfileResponse updateProfile(Long userId, UpdateUserProfileRequest request) {
        UserProfile profile = userProfileDao.findByUserIdOrThrow(userId);
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getProfilePhotoUrl() != null) profile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        if (request.getLanguagePreference() != null) profile.setLanguagePreference(request.getLanguagePreference());
        return toResponse(userProfileDao.updateProfile(profile));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .languagePreference(profile.getLanguagePreference())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
