package com.eldersphere.adminapi.service.auth;

import com.eldersphere.adminapi.dto.auth.request.LoginRequest;
import com.eldersphere.adminapi.dto.auth.request.RegisterRequest;
import com.eldersphere.adminapi.dto.auth.response.AuthResponse;
import com.eldersphere.core.context.RequestContext;
import com.eldersphere.core.dao.auth.UserDao;
import com.eldersphere.core.dao.auth.UserProfileDao;
import com.eldersphere.core.dao.auth.UserSecuritySettingsDao;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.entities.UserProfile;
import com.eldersphere.core.entities.UserSecuritySettings;
import com.eldersphere.core.enums.TwoFactorMethod;
import com.eldersphere.core.enums.UserStatus;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.UnauthorizedException;
import com.eldersphere.core.security.JwtService;
import com.eldersphere.core.security.UserPrincipal;
import com.eldersphere.core.service.TwoFactorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserDao userDao;
    private final UserProfileDao userProfileDao;
    private final UserSecuritySettingsDao userSecuritySettingsDao;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final RequestContext requestContext;
    private final TwoFactorService twoFactorService;

    @Value("${jwt.expiration}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiry;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public AuthResponse bootstrap(RegisterRequest request) throws ElderSphereException {
        long existing = userDao.countByRoleAndStatus(
                com.eldersphere.core.enums.UserRole.SUPER_ADMIN,
                com.eldersphere.core.enums.UserStatus.ACTIVE);
        if (existing > 0) {
            throw new com.eldersphere.core.exceptions.ConflictException(
                    "A SUPER_ADMIN already exists. Use POST /api/v1/auth/register with a SUPER_ADMIN token to create additional admins.");
        }
        request.setRole(com.eldersphere.core.enums.UserRole.SUPER_ADMIN);
        return register(request);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) throws ElderSphereException {
        log.info("Registering new admin user: {}", request.getEmail() != null ? request.getEmail() : request.getPhone());

        if (request.getEmail() == null && request.getPhone() == null) {
            throw BadRequestException.badRequest("Email or phone is required");
        }

        if (userDao.existsByEmailOrPhone(request.getEmail(), request.getPhone())) {
            throw BadRequestException.duplicateEmail("User already exists with the given email or phone");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        user = userDao.saveUser(user);

        // Fallback: no JWT present at registration time — set created_by = the user itself
        if (!requestContext.hasUser()) {
            requestContext.setCurrentUserId(user.getId());
            user.setCreatedBy(user);
            user.setUpdatedBy(user);
            user = userDao.updateUser(user);
        }

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .build();
        userProfileDao.saveProfile(profile);

        UserSecuritySettings settings = UserSecuritySettings.builder()
                .user(user)
                .build();
        userSecuritySettingsDao.saveSettings(settings);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) throws ElderSphereException {
        log.info("Login attempt for identifier: {}", request.getIdentifier());

        User user = resolveUser(request.getIdentifier());

        if (user == null) {
            throw UnauthorizedException.of("Invalid credentials");
        }

        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(user.getId());

        if (settings != null && isAccountLocked(settings)) {
            throw BadRequestException.accountLocked("Account is temporarily locked due to too many failed attempts");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedAttempt(settings);
            throw UnauthorizedException.of("Invalid credentials");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw UnauthorizedException.of("Account is not active");
        }

        if (settings != null) {
            userSecuritySettingsDao.resetFailedAttempts(settings);
        }

        if (twoFactorService.isTwoFactorEnabled(settings)) {
            String pendingToken = twoFactorService.storePendingToken(user.getId());
            if (settings.getTwoFactorMethod() == TwoFactorMethod.SMS
                    || settings.getTwoFactorMethod() == TwoFactorMethod.EMAIL) {
                twoFactorService.sendLoginOtp(user, settings.getTwoFactorMethod());
            }
            return AuthResponse.builder()
                    .userId(user.getId())
                    .requiresTwoFactor(true)
                    .pendingToken(pendingToken)
                    .twoFactorMethod(settings.getTwoFactorMethod())
                    .build();
        }

        return buildAuthResponse(user);
    }

    public AuthResponse completeLogin(Long userId) {
        User user = userDao.findByIdOrThrow(userId);
        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) throws ElderSphereException {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            throw UnauthorizedException.of("Invalid or expired refresh token");
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userDao.findByIdOrThrow(userId);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw UnauthorizedException.of("Account is not active");
        }

        redisTemplate.delete(key);

        return buildAuthResponse(user);
    }

    public void logout(String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
        log.info("User logged out, refresh token revoked");
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

    private User resolveUser(String identifier) {
        if (identifier.contains("@")) {
            return userDao.findByEmail(identifier);
        }
        return userDao.findByPhone(identifier);
    }

    private boolean isAccountLocked(UserSecuritySettings settings) {
        return settings.getLockedUntil() != null &&
               settings.getLockedUntil().isAfter(java.time.Instant.now());
    }

    private void handleFailedAttempt(UserSecuritySettings settings) throws ElderSphereException {
        if (settings == null) return;

        userSecuritySettingsDao.incrementFailedAttempts(settings);

        if (settings.getFailedLoginAttempts() + 1 >= MAX_FAILED_ATTEMPTS) {
            settings.setLockedUntil(java.time.Instant.now().plusSeconds(900)); // 15 min lock
            userSecuritySettingsDao.updateSettings(settings);
            log.warn("Account locked for user id: {}", settings.getUser().getId());
        }
    }
}
