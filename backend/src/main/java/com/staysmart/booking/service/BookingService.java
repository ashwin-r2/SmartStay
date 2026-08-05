package com.staysmart.booking.service;

import com.staysmart.booking.dto.BookingRequest;
import com.staysmart.booking.dto.BookingResponse;
import com.staysmart.booking.dto.CancelBookingRequest;
import com.staysmart.booking.entity.Booking;
import com.staysmart.booking.entity.BookingStatus;
import com.staysmart.booking.repository.BookingRepository;
import com.staysmart.config.AppProperties;
import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.exception.ResourceNotFoundException;
import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyStatus;
import com.staysmart.property.service.PropertyService;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Instant-book style booking engine: a booking is confirmed immediately (no separate host
 * approval step) once the requested dates are validated as free, then blocks those dates on the
 * property's availability calendar so overlapping bookings become impossible.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyService propertyService;
    private final UserService userService;
    private final AppProperties appProperties;

    @Transactional
    public BookingResponse create(Long guestId, BookingRequest request) {
        User guest = userService.getEntityById(guestId);
        Property property = propertyService.getEntityById(request.propertyId());

        if (property.getStatus() != PropertyStatus.ACTIVE) {
            throw new BadRequestException("This property is not currently accepting bookings");
        }
        if (property.getHost().getId().equals(guestId)) {
            throw new BadRequestException("You cannot book your own property");
        }
        if (!request.checkOut().isAfter(request.checkIn())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        if (request.guestsCount() > property.getMaxGuests()) {
            throw new BadRequestException("This property accommodates a maximum of " + property.getMaxGuests() + " guests");
        }
        if (!propertyService.isAvailable(property.getId(), request.checkIn(), request.checkOut())) {
            throw new BadRequestException("The selected dates are not available for this property");
        }

        int nights = (int) ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal totalPrice = property.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights))
                .add(property.getCleaningFee());

        Booking booking = Booking.builder()
                .property(property)
                .guest(guest)
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .guestsCount(request.guestsCount())
                .nights(nights)
                .pricePerNight(property.getPricePerNight())
                .cleaningFee(property.getCleaningFee())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);
        propertyService.blockForBooking(property.getId(), request.checkIn(), request.checkOut(), saved.getId());

        log.info("Guest {} booked property {} from {} to {} ({} nights, total {})",
                guestId, property.getId(), request.checkIn(), request.checkOut(), nights, totalPrice);

        return BookingResponse.from(saved);
    }

    @Transactional
    public BookingResponse cancel(Long bookingId, Long requesterId, CancelBookingRequest request) {
        Booking booking = getEntityById(bookingId);
        User requester = userService.getEntityById(requesterId);

        boolean isGuest = booking.getGuest().getId().equals(requesterId);
        boolean isHost = booking.getProperty().getHost().getId().equals(requesterId);
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isGuest && !isHost && !isAdmin) {
            throw new ForbiddenException("You are not authorized to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("A completed stay cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(request.reason());
        booking.setCancelledAt(Instant.now());
        Booking saved = bookingRepository.save(booking);

        propertyService.releaseBookingBlock(bookingId);
        log.info("Booking {} cancelled by user {} (guest={}, host={}, admin={})", bookingId, requesterId, isGuest, isHost, isAdmin);

        return BookingResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Booking getEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.of("Booking", bookingId));
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(Long bookingId, Long requesterId) {
        Booking booking = getEntityById(bookingId);
        User requester = userService.getEntityById(requesterId);
        boolean allowed = booking.getGuest().getId().equals(requesterId)
                || booking.getProperty().getHost().getId().equals(requesterId)
                || requester.getRole() == Role.ADMIN;
        if (!allowed) {
            throw new ForbiddenException("You are not authorized to view this booking");
        }
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> listByGuest(Long guestId, Pageable pageable) {
        return bookingRepository.findByGuestIdOrderByCreatedAtDesc(guestId, pageable).map(BookingResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> listByProperty(Long propertyId, Long requesterId, Pageable pageable) {
        Property property = propertyService.getEntityById(propertyId);
        User requester = userService.getEntityById(requesterId);
        boolean allowed = property.getHost().getId().equals(requesterId) || requester.getRole() == Role.ADMIN;
        if (!allowed) {
            throw new ForbiddenException("You do not own this property");
        }
        return bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId, pageable).map(BookingResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> listByHost(Long hostId, Pageable pageable) {
        return bookingRepository.findByPropertyHostId(hostId, pageable).map(BookingResponse::from);
    }

    public boolean hasCompletedStay(Long propertyId, Long guestId) {
        return bookingRepository.existsByPropertyIdAndGuestIdAndStatus(propertyId, guestId, BookingStatus.COMPLETED);
    }

    /** Runs once a day: any CONFIRMED booking whose check-out date has passed becomes COMPLETED,
     *  which is what unlocks the guest's ability to leave a review. */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void markPastBookingsCompleted() {
        List<Booking> due = bookingRepository.findByStatusAndCheckOutBefore(BookingStatus.CONFIRMED, LocalDate.now());
        due.forEach(b -> b.setStatus(BookingStatus.COMPLETED));
        if (!due.isEmpty()) {
            bookingRepository.saveAll(due);
            log.info("Marked {} past bookings as COMPLETED", due.size());
        }
    }

    // --- Admin analytics helpers --------------------------------------------------------

    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public BigDecimal totalRevenue() {
        return bookingRepository.totalRevenue();
    }

    public long countAll() {
        return bookingRepository.count();
    }

    /** Raw {@code [month, count, revenue]} rows for admin analytics; Postgres-specific (date_trunc). */
    public List<Object[]> monthlyStatsRaw() {
        return bookingRepository.monthlyBookingStats();
    }
}
