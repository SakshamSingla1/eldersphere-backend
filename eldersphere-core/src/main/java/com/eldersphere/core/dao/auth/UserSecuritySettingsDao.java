package com.eldersphere.core.dao.auth;

import com.eldersphere.core.entities.UserSecuritySettings;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.repository.UserSecuritySettingsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserSecuritySettingsDao {

    private final UserSecuritySettingsRepository userSecuritySettingsRepository;

    public UserSecuritySettingsDao(UserSecuritySettingsRepository userSecuritySettingsRepository) {
        this.userSecuritySettingsRepository = userSecuritySettingsRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public UserSecuritySettings saveSettings(UserSecuritySettings settings) throws ElderSphereException {
        try {
            return userSecuritySettingsRepository.save(settings);
        } catch (Exception e) {
            log.error("Failed to save security settings for user id {}: {}", settings.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save security settings");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public UserSecuritySettings updateSettings(UserSecuritySettings settings) throws ElderSphereException {
        try {
            return userSecuritySettingsRepository.save(settings);
        } catch (Exception e) {
            log.error("Failed to update security settings for user id {}: {}", settings.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update security settings");
        }
    }

    public UserSecuritySettings findByUserId(Long userId) {
        return userSecuritySettingsRepository.findByUserId(userId).orElse(null);
    }

    @Transactional(rollbackOn = Exception.class)
    public void incrementFailedAttempts(UserSecuritySettings settings) throws ElderSphereException {
        try {
            settings.setFailedLoginAttempts(settings.getFailedLoginAttempts() + 1);
            userSecuritySettingsRepository.save(settings);
        } catch (Exception e) {
            log.error("Failed to increment login attempts for user id {}: {}", settings.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update login attempts");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void resetFailedAttempts(UserSecuritySettings settings) throws ElderSphereException {
        try {
            settings.setFailedLoginAttempts(0);
            settings.setLockedUntil(null);
            userSecuritySettingsRepository.save(settings);
        } catch (Exception e) {
            log.error("Failed to reset login attempts for user id {}: {}", settings.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to reset login attempts");
        }
    }
}
