package com.staysmart.review.controller;

import com.staysmart.common.dto.ApiResponse;
import com.staysmart.common.dto.PageResponse;
import com.staysmart.review.dto.ReviewRequest;
import com.staysmart.review.dto.ReviewResponse;
import com.staysmart.review.service.ReviewService;
import com.staysmart.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Reviews", description = "Ratings, comments and photos left after a completed stay")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Leave a review for a completed booking")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(@AuthenticationPrincipal User currentUser,
                                               @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Review submitted", reviewService.create(currentUser.getId(), request));
    }

    @Operation(summary = "Edit your own review")
    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> update(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                               @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Review updated", reviewService.update(id, currentUser.getId(), request));
    }

    @Operation(summary = "Delete a review (owner or ADMIN)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        reviewService.delete(id, currentUser.getId());
        return ApiResponse.message("Review deleted");
    }

    @Operation(summary = "Upload photos for a review")
    @PostMapping("/{id}/images")
    public ApiResponse<List<String>> uploadImages(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
                                                    @RequestParam("files") List<MultipartFile> files) {
        return ApiResponse.ok("Images uploaded", reviewService.uploadImages(id, currentUser.getId(), files));
    }

    @Operation(summary = "List reviews for a property (public)")
    @GetMapping("/property/{propertyId}")
    public ApiResponse<PageResponse<ReviewResponse>> byProperty(@PathVariable Long propertyId,
                                                                   @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(reviewService.listByProperty(propertyId, pageable)));
    }

    @Operation(summary = "Check whether the current user may still review a given booking")
    @GetMapping("/eligibility/{bookingId}")
    public ApiResponse<Boolean> eligibility(@PathVariable Long bookingId, @AuthenticationPrincipal User currentUser) {
        return ApiResponse.ok(reviewService.canReview(bookingId, currentUser.getId()));
    }
}
