package com.staysmart.review.service;

import com.staysmart.booking.entity.Booking;
import com.staysmart.booking.entity.BookingStatus;
import com.staysmart.booking.service.BookingService;
import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ConflictException;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.exception.ResourceNotFoundException;
import com.staysmart.property.service.ImageStorageService;
import com.staysmart.property.service.PropertyService;
import com.staysmart.review.dto.ReviewRequest;
import com.staysmart.review.dto.ReviewResponse;
import com.staysmart.review.entity.Review;
import com.staysmart.review.entity.ReviewImage;
import com.staysmart.review.repository.ReviewRepository;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reviews are gated behind a completed stay: a guest may leave exactly one review per booking,
 * only after {@link BookingService} has marked that booking {@code COMPLETED}. Every create/
 * update/delete recalculates the property's cached average rating via {@link PropertyService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final PropertyService propertyService;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @Transactional
    public ReviewResponse create(Long userId, ReviewRequest request) {
        Booking booking = bookingService.getEntityById(request.bookingId());

        if (!booking.getGuest().getId().equals(userId)) {
            throw new ForbiddenException("You can only review your own bookings");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("You can only review a stay after it has been completed");
        }
        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ConflictException("You have already reviewed this booking");
        }

        User user = userService.getEntityById(userId);
        Review review = Review.builder()
                .booking(booking)
                .property(booking.getProperty())
                .user(user)
                .rating(request.rating().shortValue())
                .comment(request.comment())
                .build();

        Review saved = reviewRepository.save(review);
        refreshPropertyRating(booking.getProperty().getId());
        log.info("User {} reviewed property {} (rating={}) via booking {}",
                userId, booking.getProperty().getId(), request.rating(), booking.getId());

        return ReviewResponse.from(saved);
    }

    @Transactional
    public ReviewResponse update(Long reviewId, Long userId, ReviewRequest request) {
        Review review = getEntityById(reviewId);
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own review");
        }
        review.setRating(request.rating().shortValue());
        review.setComment(request.comment());
        Review saved = reviewRepository.save(review);
        refreshPropertyRating(review.getProperty().getId());
        return ReviewResponse.from(saved);
    }

    @Transactional
    public void delete(Long reviewId, Long requesterId) {
        Review review = getEntityById(reviewId);
        User requester = userService.getEntityById(requesterId);
        boolean isOwner = review.getUser().getId().equals(requesterId);
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not authorized to delete this review");
        }
        Long propertyId = review.getProperty().getId();
        reviewRepository.delete(review);
        refreshPropertyRating(propertyId);
        log.info("Review {} deleted by user {}", reviewId, requesterId);
    }

    @Transactional
    public List<String> uploadImages(Long reviewId, Long userId, List<MultipartFile> files) {
        Review review = getEntityById(reviewId);
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only add images to your own review");
        }
        List<String> urls = imageStorageService.storeAll(files, "reviews/" + reviewId);
        int startOrder = review.getImages().size();
        for (int i = 0; i < urls.size(); i++) {
            review.addImage(ReviewImage.builder().url(urls.get(i)).displayOrder(startOrder + i).build());
        }
        reviewRepository.save(review);
        return urls;
    }

    @Transactional(readOnly = true)
    public Review getEntityById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Review", id));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listByProperty(Long propertyId, Pageable pageable) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId, pageable).map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public boolean canReview(Long bookingId, Long userId) {
        Booking booking = bookingService.getEntityById(bookingId);
        return booking.getGuest().getId().equals(userId)
                && booking.getStatus() == BookingStatus.COMPLETED
                && !reviewRepository.existsByBookingId(bookingId);
    }

    /** Used by the AI Review Summarization feature to gather raw review text for a property. */
    @Transactional(readOnly = true)
    public List<String> getCommentsForProperty(Long propertyId, int limit) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId).stream()
                .map(Review::getComment)
                .filter(c -> c != null && !c.isBlank())
                .limit(limit)
                .toList();
    }

    public long countAll() {
        return reviewRepository.count();
    }

    private void refreshPropertyRating(Long propertyId) {
        Double avg = reviewRepository.averageRatingForProperty(propertyId);
        long count = reviewRepository.countByPropertyId(propertyId);
        propertyService.refreshRating(propertyId, BigDecimal.valueOf(avg != null ? avg : 0.0), (int) count);
    }
}
