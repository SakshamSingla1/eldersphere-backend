package com.eldersphere.core.dao.auth;

import com.eldersphere.core.entities.UserProfile;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserProfileDao {

    private final UserProfileRepository userProfileRepository;

    public UserProfileDao(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public UserProfile saveProfile(UserProfile profile) throws ElderSphereException {
        try {
            return userProfileRepository.save(profile);
        } catch (Exception e) {
            log.error("Failed to save user profile for user id {}: {}", profile.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to create user profile");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public UserProfile updateProfile(UserProfile profile) throws ElderSphereException {
        try {
            return userProfileRepository.save(profile);
        } catch (Exception e) {
            log.error("Failed to update user profile for user id {}: {}", profile.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update user profile");
        }
    }

    public UserProfile findByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId).orElse(null);
    }

    public UserProfile findByUserIdOrThrow(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("UserProfile for user", userId));
    }

    public boolean existsByUserId(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }
}
