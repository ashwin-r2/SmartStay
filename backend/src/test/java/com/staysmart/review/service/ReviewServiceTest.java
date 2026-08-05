package com.staysmart.review.service;

import com.staysmart.booking.entity.Booking;
import com.staysmart.booking.entity.BookingStatus;
import com.staysmart.booking.service.BookingService;
import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ConflictException;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.property.entity.Property;
import com.staysmart.property.service.ImageStorageService;
import com.staysmart.property.service.PropertyService;
import com.staysmart.review.dto.ReviewRequest;
import com.staysmart.review.dto.ReviewResponse;
import com.staysmart.review.entity.Review;
import com.staysmart.review.repository.ReviewRepository;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private UserService userService;
    @Mock
    private ImageStorageService imageStorageService;

    private ReviewService reviewService;

    private User guest;
    private Property property;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, bookingService, propertyService, userService, imageStorageService);
        User host = User.builder().id(1L).fullName("Host").email("host@example.com").role(Role.HOST).build();
        guest = User.builder().id(2L).fullName("Guest").email("guest@example.com").role(Role.USER).build();
        property = Property.builder().id(50L).host(host).title("Beach House").city("Goa").country("India").build();
    }

    @Test
    void create_rejectsWhenBookingNotCompleted() {
        Booking booking = Booking.builder().id(500L).property(property).guest(guest).status(BookingStatus.CONFIRMED).build();
        when(bookingService.getEntityById(500L)).thenReturn(booking);

        ReviewRequest request = new ReviewRequest(500L, 5, "Amazing stay!");

        assertThatThrownBy(() -> reviewService.create(2L, request)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void create_rejectsWhenNotBookingOwner() {
        User stranger = User.builder().id(99L).fullName("Stranger").email("stranger@example.com").role(Role.USER).build();
        Booking booking = Booking.builder().id(500L).property(property).guest(guest).status(BookingStatus.COMPLETED).build();
        when(bookingService.getEntityById(500L)).thenReturn(booking);

        ReviewRequest request = new ReviewRequest(500L, 5, "Amazing stay!");

        assertThatThrownBy(() -> reviewService.create(99L, request)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void create_rejectsDuplicateReview() {
        Booking booking = Booking.builder().id(500L).property(property).guest(guest).status(BookingStatus.COMPLETED).build();
        when(bookingService.getEntityById(500L)).thenReturn(booking);
        when(reviewRepository.existsByBookingId(500L)).thenReturn(true);

        ReviewRequest request = new ReviewRequest(500L, 5, "Amazing stay!");

        assertThatThrownBy(() -> reviewService.create(2L, request)).isInstanceOf(ConflictException.class);
    }

    @Test
    void create_savesReviewAndRefreshesPropertyRating() {
        Booking booking = Booking.builder().id(500L).property(property).guest(guest).status(BookingStatus.COMPLETED).build();
        when(bookingService.getEntityById(500L)).thenReturn(booking);
        when(reviewRepository.existsByBookingId(500L)).thenReturn(false);
        when(userService.getEntityById(2L)).thenReturn(guest);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(900L);
            return r;
        });
        when(reviewRepository.averageRatingForProperty(50L)).thenReturn(5.0);
        when(reviewRepository.countByPropertyId(50L)).thenReturn(1L);

        ReviewRequest request = new ReviewRequest(500L, 5, "Amazing stay!");
        ReviewResponse response = reviewService.create(2L, request);

        assertThat(response.id()).isEqualTo(900L);
        assertThat(response.rating()).isEqualTo(5);
        org.mockito.Mockito.verify(propertyService).refreshRating(50L, java.math.BigDecimal.valueOf(5.0), 1);
    }
}
