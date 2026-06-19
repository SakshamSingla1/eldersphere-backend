package com.eldersphere.adminapi.controller.auth;

import com.eldersphere.adminapi.dto.auth.request.LoginRequest;
import com.eldersphere.adminapi.dto.auth.request.RegisterRequest;
import com.eldersphere.adminapi.dto.auth.request.SocialAuthRequest;
import com.eldersphere.adminapi.dto.auth.request.SuperAdminRequest;
import com.eldersphere.adminapi.dto.auth.response.AuthResponse;
import com.eldersphere.adminapi.service.auth.AuthService;
import com.eldersphere.adminapi.service.auth.SocialAuthService;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.models.ResponseModel;
import com.eldersphere.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * One-time setup: creates the very first SUPER_ADMIN when none exists.
     * Returns 409 Conflict if a SUPER_ADMIN already exists.
     * The role field in the request body is ignored — always creates SUPER_ADMIN.
     */
    @PostMapping("/super-admin")
    public ResponseEntity<ResponseModel<AuthResponse>> bootstrap(
            @Valid @RequestBody SuperAdminRequest request) throws ElderSphereException {
        log.info("Bootstrap SUPER_ADMIN request for: {}", request.getEmail() != null ? request.getEmail() : request.getPhone());
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPhone(request.getPhone());
        registerRequest.setPassword(request.getPassword());
        registerRequest.setFullName(request.getFullName());
        AuthResponse response = authService.bootstrap(registerRequest);
        return ApiResponse.createSuccess(response, "SUPER_ADMIN created successfully");
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) throws ElderSphereException {
        log.info("Register request for: {}", request.getEmail() != null ? request.getEmail() : request.getPhone());
        AuthResponse response = authService.register(request);
        return ApiResponse.createSuccess(response, "Admin user registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) throws ElderSphereException {
        log.info("Login request for identifier: {}", request.getIdentifier());
        AuthResponse response = authService.login(request);
        return ApiResponse.successResponse(response, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseModel<AuthResponse>> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) throws ElderSphereException {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ApiResponse.successResponse(response, "Token refreshed successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseModel<Void>> logout(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
        return ApiResponse.successResponse();
    }

    /**
     * Social / OAuth login via Firebase ID token.
     * <p>
     * Client-side flow:
     * 1. User signs in with Google / Facebook / Apple using Firebase SDK on mobile/web.
     * 2. Firebase returns a short-lived ID token.
     * 3. Client POSTs that token here with the provider and (for new users) desired role.
     * <p>
     * On new users: creates the account automatically (default role = ELDER if not specified).
     * On existing users: links the social account if not already linked, then logs in.
     */
    @PostMapping("/oauth")
    public ResponseEntity<ResponseModel<AuthResponse>> oauthLogin(
            @Valid @RequestBody SocialAuthRequest request) throws ElderSphereException {
        log.info("OAuth login request: provider={}", request.getProvider());
        AuthResponse response = socialAuthService.socialLogin(request);
        return ApiResponse.successResponse(response, "OAuth login successful");
    }
}
