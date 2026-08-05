package com.staysmart.booking.controller;

import com.staysmart.booking.dto.BookingRequest;
import com.staysmart.booking.dto.BookingResponse;
import com.staysmart.booking.dto.CancelBookingRequest;
import com.staysmart.booking.service.BookingService;
import com.staysmart.common.dto.ApiResponse;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookings", description = "Search availability, book a stay, view history, cancel")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Book a property (instant confirmation)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingResponse> create(@AuthenticationPrincipal User currentUser,
                                                @Valid @RequestBody BookingRequest request) {
        return ApiResponse.ok("Booking confirmed", bookingService.create(currentUser.getId(), request));
    }

    @Operation(summary = "Get a single booking's details")
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ApiResponse.ok(bookingService.getById(id, currentUser.getId()));
    }

    @Operation(summary = "Cancel a booking")
    @PutMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                @Valid @RequestBody CancelBookingRequest request) {
        return ApiResponse.ok("Booking cancelled", bookingService.cancel(id, currentUser.getId(), request));
    }

    @Operation(summary = "Get the current user's booking history")
    @GetMapping("/me")
    public ApiResponse<PageResponse<BookingResponse>> myBookings(@AuthenticationPrincipal User currentUser,
                                                                   @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(bookingService.listByGuest(currentUser.getId(), pageable)));
    }

    @Operation(summary = "Get all bookings for a property (host/admin only)")
    @GetMapping("/property/{propertyId}")
    public ApiResponse<PageResponse<BookingResponse>> byProperty(@PathVariable Long propertyId,
                                                                    @AuthenticationPrincipal User currentUser,
                                                                    @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(bookingService.listByProperty(propertyId, currentUser.getId(), pageable)));
    }

    @Operation(summary = "Get all bookings across every property the current user hosts")
    @GetMapping("/host/me")
    public ApiResponse<PageResponse<BookingResponse>> myHostBookings(@AuthenticationPrincipal User currentUser,
                                                                       @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(bookingService.listByHost(currentUser.getId(), pageable)));
    }
}
