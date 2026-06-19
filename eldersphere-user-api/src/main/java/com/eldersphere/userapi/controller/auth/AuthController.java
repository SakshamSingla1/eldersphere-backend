package com.eldersphere.userapi.controller.auth;

import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import com.eldersphere.userapi.dto.auth.request.LoginRequest;
import com.eldersphere.userapi.dto.auth.request.RegisterRequest;
import com.eldersphere.userapi.dto.auth.request.SocialAuthRequest;
import com.eldersphere.userapi.dto.auth.response.AuthResponse;
import com.eldersphere.userapi.service.auth.AuthService;
import com.eldersphere.userapi.service.auth.SocialAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final SocialAuthService socialAuthService;

    /**
     * Self-registration. Allowed roles: ELDER, FAMILY_MEMBER, CARETAKER.
     * Open to all — no token required.
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) throws ElderSphereException {
        log.info("Register: {}", request.getEmail() != null ? request.getEmail() : request.getPhone());
        return ApiResponse.createSuccess(authService.register(request), "Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) throws ElderSphereException {
        log.info("Login: {}", request.getIdentifier());
        return ApiResponse.successResponse(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseModel<AuthResponse>> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) throws ElderSphereException {
        return ApiResponse.successResponse(authService.refreshToken(refreshToken), "Token refreshed successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseModel<Void>> logout(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
        return ApiResponse.successResponse();
    }

    /**
     * Social login via Firebase ID token (Google / Facebook / Apple).
     * Creates a new account automatically if the user does not exist.
     */
    @PostMapping("/oauth")
    public ResponseEntity<ResponseModel<AuthResponse>> oauthLogin(
            @Valid @RequestBody SocialAuthRequest request) throws ElderSphereException {
        log.info("OAuth login: provider={}", request.getProvider());
        return ApiResponse.successResponse(socialAuthService.socialLogin(request), "OAuth login successful");
    }
}
