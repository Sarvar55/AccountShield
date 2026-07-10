package com.codems.accountshield.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.domain.base.BaseResponse;
import com.codems.accountshield.domain.auth.dto.AuthResponse;
import com.codems.accountshield.domain.auth.dto.EmailVerificationRequest;
import com.codems.accountshield.domain.auth.dto.LoginRequest;
import com.codems.accountshield.domain.auth.dto.RegistrationResult;
import com.codems.accountshield.domain.auth.dto.RefreshRequest;
import com.codems.accountshield.domain.auth.dto.RegisterRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegistrationResult result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(result.user(), HttpStatus.CREATED, result.message()));
    }

    @PostMapping(value = "/login", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(BaseResponse.success(authService.login(request)));
    }

    @PostMapping(value = "/refresh", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(BaseResponse.success(authService.refresh(request)));
    }

    @PostMapping(value = "/verify-email", version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        return ResponseEntity.ok(BaseResponse.success(authService.verifyEmail(request)));
    }
}
