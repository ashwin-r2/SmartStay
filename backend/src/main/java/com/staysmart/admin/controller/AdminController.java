package com.staysmart.admin.controller;

import com.staysmart.admin.dto.AiUsageStatsResponse;
import com.staysmart.admin.dto.BookingAnalyticsResponse;
import com.staysmart.admin.dto.DashboardStatsResponse;
import com.staysmart.admin.service.AdminService;
import com.staysmart.common.dto.ApiResponse;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.PropertySummaryResponse;
import com.staysmart.property.service.PropertyService;
import com.staysmart.user.dto.UserResponse;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only endpoints: platform-wide user/property management and analytics. Restricted both at
 * the URL level ({@code SecurityConfig}'s {@code /admin/**} rule) and per-method for defense in depth.
 */
@Tag(name = "Admin", description = "Manage users/properties and view booking + AI usage analytics (ADMIN only)")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final PropertyService propertyService;

    @Operation(summary = "Platform-wide dashboard totals")
    @GetMapping("/dashboard")
    public ApiResponse<DashboardStatsResponse> dashboard() {
        return ApiResponse.ok(adminService.dashboardStats());
    }

    @Operation(summary = "List all users, optionally filtered by role")
    @GetMapping("/users")
    public ApiResponse<PageResponse<UserResponse>> users(@RequestParam(required = false) Role role,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(userService.list(role, pageable)));
    }

    @Operation(summary = "Enable or disable a user account")
    @PatchMapping("/users/{id}/enabled")
    public ApiResponse<UserResponse> setUserEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.ok(userService.setEnabled(id, enabled));
    }

    @Operation(summary = "Change a user's role")
    @PatchMapping("/users/{id}/role")
    public ApiResponse<UserResponse> changeUserRole(@PathVariable Long id, @RequestParam Role role) {
        return ApiResponse.ok(userService.changeRole(id, role));
    }

    @Operation(summary = "Delete a user account")
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.message("User deleted");
    }

    @Operation(summary = "List all properties on the platform, including inactive ones")
    @GetMapping("/properties")
    public ApiResponse<PageResponse<PropertySummaryResponse>> properties(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(propertyService.listAllForAdmin(pageable)));
    }

    @Operation(summary = "Delete any property listing")
    @DeleteMapping("/properties/{id}")
    public ApiResponse<Void> deleteProperty(@PathVariable Long id, @AuthenticationPrincipal User currentAdmin) {
        propertyService.delete(id, currentAdmin.getId());
        return ApiResponse.message("Property deleted");
    }

    @Operation(summary = "Booking analytics: totals by status, revenue, monthly trend")
    @GetMapping("/bookings/analytics")
    public ApiResponse<BookingAnalyticsResponse> bookingAnalytics() {
        return ApiResponse.ok(adminService.bookingAnalytics());
    }

    @Operation(summary = "AI usage statistics: calls, success rate and latency per feature")
    @GetMapping("/ai/usage")
    public ApiResponse<AiUsageStatsResponse> aiUsage() {
        return ApiResponse.ok(adminService.aiUsageStats());
    }
}
