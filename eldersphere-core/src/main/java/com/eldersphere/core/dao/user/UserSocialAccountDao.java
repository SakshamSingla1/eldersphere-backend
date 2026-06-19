package com.eldersphere.core.dao.user;

import com.eldersphere.core.entities.UserSocialAccount;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.enums.SocialProvider;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.repository.UserSocialAccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class UserSocialAccountDao {

    private final UserSocialAccountRepository userSocialAccountRepository;

    public UserSocialAccountDao(UserSocialAccountRepository userSocialAccountRepository) {
        this.userSocialAccountRepository = userSocialAccountRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public UserSocialAccount save(UserSocialAccount account) throws ElderSphereException {
        try {
            return userSocialAccountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate social account for provider {}: {}", account.getProvider(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.DUPLICATE_ENTRY, "Social account already linked");
        } catch (Exception e) {
            log.error("Failed to save social account: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to link social account");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ElderSphereException {
        try {
            userSocialAccountRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete social account id {}: {}", id, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to unlink social account");
        }
    }

    public UserSocialAccount findByProviderAndProviderUserId(SocialProvider provider, String providerUserId) {
        return userSocialAccountRepository.findByProviderAndProviderUserId(provider, providerUserId).orElse(null);
    }

    public List<UserSocialAccount> findAllByUserId(Long userId) {
        return userSocialAccountRepository.findAllByUserId(userId);
    }

    public boolean existsByUserIdAndProvider(Long userId, SocialProvider provider) {
        return userSocialAccountRepository.existsByUserIdAndProvider(userId, provider);
    }
}
