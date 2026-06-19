package com.eldersphere.core.repository;

import com.eldersphere.core.entities.UserSocialAccount;
import com.eldersphere.core.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {

    Optional<UserSocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);

    List<UserSocialAccount> findAllByUserId(Long userId);

    boolean existsByUserIdAndProvider(Long userId, SocialProvider provider);
}
