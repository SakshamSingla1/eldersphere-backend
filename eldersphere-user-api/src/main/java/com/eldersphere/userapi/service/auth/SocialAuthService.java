package com.eldersphere.userapi.service.auth;

import com.eldersphere.core.dao.auth.UserDao;
import com.eldersphere.core.dao.auth.UserProfileDao;
import com.eldersphere.core.dao.auth.UserSecuritySettingsDao;
import com.eldersphere.core.dao.user.UserSocialAccountDao;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.entities.UserProfile;
import com.eldersphere.core.entities.UserSecuritySettings;
import com.eldersphere.core.entities.UserSocialAccount;
import com.eldersphere.core.enums.SocialProvider;
import com.eldersphere.core.enums.UserRole;
import com.eldersphere.core.enums.UserStatus;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.UnauthorizedException;
import com.eldersphere.core.security.JwtService;
import com.eldersphere.core.security.UserPrincipal;
import com.eldersphere.userapi.dto.auth.request.SocialAuthRequest;
import com.eldersphere.userapi.dto.auth.response.AuthResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAuthService {

    private final UserDao userDao;
    private final UserProfileDao userProfileDao;
    private final UserSecuritySettingsDao userSecuritySettingsDao;
    private final UserSocialAccountDao userSocialAccountDao;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.expiration}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiry;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final Set<UserRole> ALLOWED_ROLES =
            Set.of(UserRole.ELDER, UserRole.FAMILY_MEMBER, UserRole.CARETAKER);

    @Transactional
    public AuthResponse socialLogin(SocialAuthRequest request) throws ElderSphereException {
        FirebaseToken firebaseToken = verifyFirebaseToken(request.getIdToken());

        String providerUserId = firebaseToken.getUid();
        String email = firebaseToken.getEmail();
        String displayName = firebaseToken.getName();
        String photoUrl = firebaseToken.getPicture();
        SocialProvider provider = resolveProvider(firebaseToken, request.getProvider());

        log.info("OAuth login: provider={}, uid={}, email={}", provider, providerUserId, email);

        UserSocialAccount socialAccount = userSocialAccountDao
                .findByProviderAndProviderUserId(provider, providerUserId);

        User user;
        if (socialAccount != null) {
            user = socialAccount.getUser();
        } else {
            user = (email != null) ? userDao.findByEmail(email) : null;

            if (user == null) {
                UserRole role = resolveRole(request.getRole());
                user = createUser(email, role, displayName);
            }

            linkSocialAccount(user, provider, providerUserId, photoUrl);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw UnauthorizedException.of("Account is not active");
        }

        return buildAuthResponse(user);
    }

    private FirebaseToken verifyFirebaseToken(String idToken) {
        if (com.google.firebase.FirebaseApp.getApps().isEmpty()) {
            throw UnauthorizedException.of("OAuth login is not configured on this server");
        }
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            log.warn("Firebase token verification failed: {}", e.getMessage());
            throw UnauthorizedException.of("Invalid or expired social token");
        }
    }

    private SocialProvider resolveProvider(FirebaseToken token, SocialProvider fallback) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> firebase = (Map<String, Object>) token.getClaims().get("firebase");
            if (firebase == null) return fallback;
            String signInProvider = (String) firebase.get("sign_in_provider");
            if (signInProvider == null) return fallback;
            return switch (signInProvider) {
                case "google.com" -> SocialProvider.GOOGLE;
                case "facebook.com" -> SocialProvider.FACEBOOK;
                case "apple.com" -> SocialProvider.APPLE;
                default -> fallback;
            };
        } catch (Exception e) {
            return fallback;
        }
    }

    private UserRole resolveRole(UserRole requested) {
        if (requested == null) return UserRole.ELDER;
        if (!ALLOWED_ROLES.contains(requested)) {
            throw BadRequestException.badRequest("Role must be one of: ELDER, FAMILY_MEMBER, CARETAKER");
        }
        return requested;
    }

    private User createUser(String email, UserRole role, String displayName) throws ElderSphereException {
        User user = User.builder()
                .email(email)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        user = userDao.saveUser(user);

        user.setCreatedBy(user);
        user.setUpdatedBy(user);
        user = userDao.updateUser(user);

        userProfileDao.saveProfile(UserProfile.builder()
                .user(user)
                .fullName(displayName != null ? displayName : "")
                .build());

        userSecuritySettingsDao.saveSettings(UserSecuritySettings.builder()
                .user(user)
                .build());

        return user;
    }

    private void linkSocialAccount(User user, SocialProvider provider,
                                   String providerUserId, String photoUrl) throws ElderSphereException {
        userSocialAccountDao.save(UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .accessTokenEnc(photoUrl)
                .linkedAt(Instant.now())
                .build());

        UserProfile profile = userProfileDao.findByUserId(user.getId());
        if (profile != null && profile.getProfilePhotoUrl() == null && photoUrl != null) {
            profile.setProfilePhotoUrl(photoUrl);
            userProfileDao.updateProfile(profile);
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken();

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + refreshToken,
                String.valueOf(user.getId()),
                refreshTokenExpiry,
                TimeUnit.MILLISECONDS
        );

        UserProfile profile = userProfileDao.findByUserId(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(profile != null ? profile.getFullName() : null)
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiry / 1000)
                .build();
    }
}
