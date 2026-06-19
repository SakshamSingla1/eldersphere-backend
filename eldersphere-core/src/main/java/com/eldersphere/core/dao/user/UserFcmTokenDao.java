package com.eldersphere.core.dao.user;

import com.eldersphere.core.entities.UserFcmToken;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.repository.UserFcmTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class UserFcmTokenDao {

    private final UserFcmTokenRepository userFcmTokenRepository;

    public UserFcmTokenDao(UserFcmTokenRepository userFcmTokenRepository) {
        this.userFcmTokenRepository = userFcmTokenRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public UserFcmToken save(UserFcmToken token) throws ElderSphereException {
        try {
            return userFcmTokenRepository.save(token);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate FCM token for user id {}, skipping", token.getUser().getId());
            return token;
        } catch (Exception e) {
            log.error("Failed to save FCM token for user id {}: {}", token.getUser().getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to register device token");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deactivateByToken(String token) {
        try {
            userFcmTokenRepository.deactivateByToken(token);
        } catch (Exception e) {
            log.warn("Failed to deactivate FCM token: {}", e.getMessage());
        }
    }

    public List<UserFcmToken> findActiveByUserId(Long userId) {
        return userFcmTokenRepository.findAllByUserIdAndIsActiveTrue(userId);
    }

    public boolean existsByUserIdAndToken(Long userId, String token) {
        return userFcmTokenRepository.existsByUserIdAndToken(userId, token);
    }
}
