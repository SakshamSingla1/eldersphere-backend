package com.eldersphere.adminapi.service.auth;

import com.eldersphere.adminapi.dto.auth.request.SocialAuthRequest;
import com.eldersphere.adminapi.dto.auth.response.AuthResponse;
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
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.UnauthorizedException;
import com.eldersphere.core.security.JwtService;
import com.eldersphere.core.security.UserPrincipal;
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

    @Transactional
    public AuthResponse socialLogin(SocialAuthRequest request) throws ElderSphereException {
        FirebaseToken firebaseToken = verifyFirebaseToken(request.getIdToken());

        String providerUserId = firebaseToken.getUid();
        String email = firebaseToken.getEmail();
        String displayName = firebaseToken.getName();
        String photoUrl = firebaseToken.getPicture();
        SocialProvider provider = resolveProvider(firebaseToken, request.getProvider());

        log.info("OAuth login: provider={}, uid={}, email={}", provider, providerUserId, email);

        // 1. Check if social account already linked
        UserSocialAccount socialAccount = userSocialAccountDao
                .findByProviderAndProviderUserId(provider, providerUserId);

        User user;
        if (socialAccount != null) {
            user = socialAccount.getUser();
            log.info("Existing social account found for user id={}", user.getId());
        } else {
            // 2. Try to find existing user by email
            user = (email != null) ? userDao.findByEmail(email) : null;

            if (user != null) {
                log.info("Linking new social account to existing user id={}", user.getId());
            } else {
                // 3. Create brand-new user
                user = createUser(email, request.getRole(), displayName);
                log.info("New user created via OAuth: id={}", user.getId());
            }

            // 4. Link social account
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

    /**
     * Firebase's sign_in_provider claim returns "google.com", "facebook.com", "apple.com".
     * Map to our enum; fall back to the request's declared provider.
     */
    private SocialProvider resolveProvider(FirebaseToken token, SocialProvider fallback) {
        try {
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

    private User createUser(String email, UserRole requestedRole, String displayName) throws ElderSphereException {
        UserRole role = (requestedRole != null) ? requestedRole : UserRole.ELDER;

        User user = User.builder()
                .email(email)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        user = userDao.saveUser(user);

        // Self-referencing audit: new user is its own creator
        user.setCreatedBy(user);
        user.setUpdatedBy(user);
        user = userDao.updateUser(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(displayName != null ? displayName : "")
                .build();
        userProfileDao.saveProfile(profile);

        UserSecuritySettings settings = UserSecuritySettings.builder()
                .user(user)
                .build();
        userSecuritySettingsDao.saveSettings(settings);

        return user;
    }

    private void linkSocialAccount(User user, SocialProvider provider,
                                   String providerUserId, String photoUrl) throws ElderSphereException {
        UserSocialAccount account = UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .accessTokenEnc(photoUrl)   // store photoUrl in accessTokenEnc field; extend entity if needed
                .linkedAt(Instant.now())
                .build();
        userSocialAccountDao.save(account);

        // Update profile photo if not already set
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
