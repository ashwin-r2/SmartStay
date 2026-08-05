package com.staysmart.property.controller;

import com.staysmart.common.dto.ApiResponse;
import com.staysmart.property.dto.AmenityRequest;
import com.staysmart.property.dto.AmenityResponse;
import com.staysmart.property.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Amenities", description = "Reusable amenity catalog (WiFi, Pool, Parking, ...)")
@RestController
@RequestMapping("/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @Operation(summary = "List all amenities")
    @GetMapping
    public ApiResponse<List<AmenityResponse>> listAll() {
        return ApiResponse.ok(amenityService.listAll());
    }

    @Operation(summary = "Create a new amenity (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AmenityResponse> create(@Valid @RequestBody AmenityRequest request) {
        return ApiResponse.ok("Amenity created", amenityService.create(request));
    }

    @Operation(summary = "Delete an amenity (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        amenityService.delete(id);
        return ApiResponse.message("Amenity deleted");
    }
}
