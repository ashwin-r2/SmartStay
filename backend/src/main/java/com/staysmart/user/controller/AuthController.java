package com.staysmart.user.controller;

import com.staysmart.common.dto.ApiResponse;
import com.staysmart.user.dto.AuthResponse;
import com.staysmart.user.dto.LoginRequest;
import com.staysmart.user.dto.RefreshTokenRequest;
import com.staysmart.user.dto.RegisterRequest;
import com.staysmart.user.dto.UserResponse;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Registration, login, token refresh and logout")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new USER or HOST account")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registration successful", authService.register(request));
    }

    @Operation(summary = "Log in with email and password")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }

    @Operation(summary = "Exchange a valid refresh token for a new access/refresh token pair")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "Revoke a refresh token")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.message("Logged out successfully");
    }

    @Operation(summary = "Get the currently authenticated user's profile")
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal User currentUser) {
        return ApiResponse.ok(UserResponse.from(currentUser));
    }
}
