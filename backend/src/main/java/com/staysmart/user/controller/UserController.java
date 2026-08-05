package com.staysmart.user.controller;

import com.staysmart.common.dto.ApiResponse;
import com.staysmart.user.dto.ChangePasswordRequest;
import com.staysmart.user.dto.UpdateProfileRequest;
import com.staysmart.user.dto.UserResponse;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Self-service profile management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a user's public profile by id")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @Operation(summary = "Update the current user's profile")
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateProfile(@AuthenticationPrincipal User currentUser,
                                                     @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(currentUser.getId(), request));
    }

    @Operation(summary = "Change the current user's password")
    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal User currentUser,
                                             @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser.getId(), request);
        return ApiResponse.message("Password changed successfully");
    }
}
