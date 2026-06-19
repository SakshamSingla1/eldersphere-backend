package com.eldersphere.userapi.controller.auth;

import com.eldersphere.core.context.RequestContext;
import com.eldersphere.core.dao.auth.UserDao;
import com.eldersphere.core.dao.auth.UserSecuritySettingsDao;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.entities.UserSecuritySettings;
import com.eldersphere.core.enums.TwoFactorMethod;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.service.TotpSetupResult;
import com.eldersphere.core.service.TwoFactorService;
import com.eldersphere.core.utils.ApiResponse;
import com.eldersphere.userapi.dto.auth.request.ConfirmOtpRequest;
import com.eldersphere.userapi.dto.auth.request.InitiateOtpSetupRequest;
import com.eldersphere.userapi.dto.auth.request.TwoFactorVerifyRequest;
import com.eldersphere.userapi.dto.auth.response.AuthResponse;
import com.eldersphere.userapi.dto.auth.response.TotpSetupResponse;
import com.eldersphere.userapi.dto.auth.response.TwoFactorStatusResponse;
import com.eldersphere.userapi.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/2fa")
@RequiredArgsConstructor
@Slf4j
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final AuthService authService;
    private final UserDao userDao;
    private final UserSecuritySettingsDao userSecuritySettingsDao;
    private final RequestContext requestContext;

    /** POST /api/v1/auth/2fa/setup/totp — initiate TOTP setup */
    @PostMapping("/setup/totp")
    public ResponseEntity<ResponseModel<TotpSetupResponse>> initiateTotp() {
        Long userId = requestContext.getCurrentUserId();
        User user = userDao.findByIdOrThrow(userId);
        UserSecuritySettings settings = getOrCreateSettings(user);

        TotpSetupResult result = twoFactorService.initiateTotp(user, settings);

        TotpSetupResponse response = TotpSetupResponse.builder()
                .secret(result.getSecret())
                .qrUri(result.getQrUri())
                .qrCodeBase64(result.getQrCodeBase64())
                .method(TwoFactorMethod.TOTP.name())
                .build();
        return ApiResponse.successResponse(response, "TOTP setup initiated. Scan the QR code and confirm with a code.");
    }

    /** POST /api/v1/auth/2fa/setup/totp/confirm — confirm TOTP code and enable */
    @PostMapping("/setup/totp/confirm")
    public ResponseEntity<ResponseModel<Void>> confirmTotp(@Valid @RequestBody ConfirmOtpRequest request) {
        Long userId = requestContext.getCurrentUserId();
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(userId);
        if (settings == null) throw BadRequestException.badRequest("Security settings not found");

        twoFactorService.confirmTotp(settings, request.getCode());
        return ApiResponse.successResponse(null, "TOTP two-factor authentication enabled successfully");
    }

    /** POST /api/v1/auth/2fa/setup/otp — initiate SMS/EMAIL 2FA setup */
    @PostMapping("/setup/otp")
    public ResponseEntity<ResponseModel<Void>> initiateOtp(@Valid @RequestBody InitiateOtpSetupRequest request) {
        Long userId = requestContext.getCurrentUserId();
        User user = userDao.findByIdOrThrow(userId);
        UserSecuritySettings settings = getOrCreateSettings(user);

        twoFactorService.initiateOtpSetup(user, settings, request.getMethod());
        return ApiResponse.successResponse(null, "Verification code sent. Please confirm to enable 2FA.");
    }

    /** POST /api/v1/auth/2fa/setup/otp/confirm — confirm OTP and enable SMS/EMAIL 2FA */
    @PostMapping("/setup/otp/confirm")
    public ResponseEntity<ResponseModel<Void>> confirmOtp(@Valid @RequestBody ConfirmOtpRequest request) {
        Long userId = requestContext.getCurrentUserId();
        User user = userDao.findByIdOrThrow(userId);
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(userId);
        if (settings == null) throw BadRequestException.badRequest("Security settings not found");

        twoFactorService.confirmOtpSetup(user, settings, request.getCode());
        return ApiResponse.successResponse(null, "Two-factor authentication enabled successfully");
    }

    /** POST /api/v1/auth/2fa/verify — verify 2FA code during login (public) */
    @PostMapping("/verify")
    public ResponseEntity<ResponseModel<AuthResponse>> verify(@Valid @RequestBody TwoFactorVerifyRequest request) {
        Long userId = twoFactorService.resolvePendingToken(request.getPendingToken());
        User user = userDao.findByIdOrThrow(userId);
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(userId);
        if (settings == null) throw BadRequestException.badRequest("Security settings not found");

        TwoFactorMethod method = settings.getTwoFactorMethod();
        if (method == TwoFactorMethod.TOTP) {
            if (!twoFactorService.verifyTotpCode(settings.getTotpSecretEncrypted(), request.getCode())) {
                throw BadRequestException.badRequest("Invalid TOTP code");
            }
        } else {
            twoFactorService.verifyLoginOtp(user, method, request.getCode());
        }

        twoFactorService.deletePendingToken(request.getPendingToken());
        AuthResponse authResponse = authService.completeLogin(userId);
        return ApiResponse.successResponse(authResponse, "2FA verified successfully");
    }

    /** DELETE /api/v1/auth/2fa — disable 2FA */
    @DeleteMapping
    public ResponseEntity<ResponseModel<Void>> disable() {
        Long userId = requestContext.getCurrentUserId();
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(userId);
        if (settings == null) throw BadRequestException.badRequest("Security settings not found");

        twoFactorService.disable(settings);
        return ApiResponse.successResponse(null, "Two-factor authentication disabled");
    }

    /** GET /api/v1/auth/2fa/status — get 2FA status */
    @GetMapping("/status")
    public ResponseEntity<ResponseModel<TwoFactorStatusResponse>> status() {
        Long userId = requestContext.getCurrentUserId();
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(userId);

        TwoFactorStatusResponse response = TwoFactorStatusResponse.builder()
                .enabled(settings != null && settings.isTotpEnabled())
                .method(settings != null ? settings.getTwoFactorMethod() : null)
                .build();
        return ApiResponse.successResponse(response, "2FA status retrieved");
    }

    private UserSecuritySettings getOrCreateSettings(User user) {
        UserSecuritySettings settings = userSecuritySettingsDao.findByUserId(user.getId());
        if (settings == null) {
            settings = UserSecuritySettings.builder().user(user).build();
            userSecuritySettingsDao.saveSettings(settings);
        }
        return settings;
    }
}
