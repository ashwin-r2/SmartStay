package com.staysmart.property.controller;

import com.staysmart.common.dto.ApiResponse;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.property.dto.AvailabilityBlockRequest;
import com.staysmart.property.dto.AvailabilityBlockResponse;
import com.staysmart.property.dto.PropertyRequest;
import com.staysmart.property.dto.PropertyResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.dto.PropertySummaryResponse;
import com.staysmart.property.entity.PropertyStatus;
import com.staysmart.property.service.PropertyService;
import com.staysmart.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Properties", description = "Property CRUD, search and availability calendar")
@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Search active properties with filters (city, dates, guests, price, amenities, keyword)")
    @GetMapping("/search")
    public ApiResponse<PageResponse<PropertySummaryResponse>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) List<Long> amenityIds,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {

        PropertySearchCriteria criteria = new PropertySearchCriteria(
                city, country, checkIn, checkOut, guests, minPrice, maxPrice, propertyType, roomType, amenityIds, keyword);
        return ApiResponse.ok(PageResponse.from(propertyService.search(criteria, pageable)));
    }

    @Operation(summary = "Get full property details by id")
    @GetMapping("/{id}")
    public ApiResponse<PropertyResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(propertyService.getById(id));
    }

    @Operation(summary = "List properties owned by a host")
    @GetMapping("/host/{hostId}")
    public ApiResponse<PageResponse<PropertySummaryResponse>> byHost(@PathVariable Long hostId,
                                                                       @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(propertyService.listByHost(hostId, pageable)));
    }

    @Operation(summary = "Create a new property listing (HOST/ADMIN only)")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PropertyResponse> create(@AuthenticationPrincipal User currentUser,
                                                 @Valid @RequestBody PropertyRequest request) {
        return ApiResponse.ok("Property created", propertyService.create(currentUser.getId(), request));
    }

    @Operation(summary = "Update a property listing (owner HOST or ADMIN only)")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<PropertyResponse> update(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                 @Valid @RequestBody PropertyRequest request) {
        return ApiResponse.ok("Property updated", propertyService.update(id, currentUser.getId(), request));
    }

    @Operation(summary = "Activate/deactivate a listing")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<PropertyResponse> setStatus(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                     @RequestParam PropertyStatus status) {
        return ApiResponse.ok(propertyService.setStatus(id, currentUser.getId(), status));
    }

    @Operation(summary = "Delete a property listing")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        propertyService.delete(id, currentUser.getId());
        return ApiResponse.message("Property deleted");
    }

    @Operation(summary = "Upload one or more images for a property")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PostMapping("/{id}/images")
    public ApiResponse<List<String>> uploadImages(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                    @RequestParam("files") List<MultipartFile> files) {
        return ApiResponse.ok("Images uploaded", propertyService.uploadImages(id, currentUser.getId(), files));
    }

    @Operation(summary = "Remove a property image")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @DeleteMapping("/{id}/images/{imageId}")
    public ApiResponse<Void> removeImage(@PathVariable Long id, @PathVariable Long imageId,
                                          @AuthenticationPrincipal User currentUser) {
        propertyService.removeImage(id, imageId, currentUser.getId());
        return ApiResponse.message("Image removed");
    }

    @Operation(summary = "Get a property's availability calendar (blocked/booked date ranges)")
    @GetMapping("/{id}/availability")
    public ApiResponse<List<AvailabilityBlockResponse>> availability(@PathVariable Long id) {
        return ApiResponse.ok(propertyService.getAvailability(id));
    }

    @Operation(summary = "Block a date range on the calendar (host maintenance/personal use)")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PostMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AvailabilityBlockResponse> blockDates(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                               @Valid @RequestBody AvailabilityBlockRequest request) {
        return ApiResponse.ok(propertyService.blockDates(id, currentUser.getId(), request));
    }

    @Operation(summary = "Remove a manual availability block")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @DeleteMapping("/{id}/availability/{blockId}")
    public ApiResponse<Void> unblockDates(@PathVariable Long id, @PathVariable Long blockId,
                                           @AuthenticationPrincipal User currentUser) {
        propertyService.unblockDates(id, currentUser.getId(), blockId);
        return ApiResponse.message("Availability block removed");
    }
}
