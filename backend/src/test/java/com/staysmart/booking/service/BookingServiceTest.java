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
import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyStatus;
import com.staysmart.property.service.PropertyService;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyService propertyService;
    @Mock
    private UserService userService;

    private BookingService bookingService;

    private User guest;
    private User host;
    private Property property;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, propertyService, userService, new AppProperties());

        host = User.builder().id(1L).fullName("Host").email("host@example.com").role(Role.HOST).build();
        guest = User.builder().id(2L).fullName("Guest").email("guest@example.com").role(Role.USER).build();
        property = Property.builder().id(50L).host(host).title("Beach House").city("Goa").country("India")
                .status(PropertyStatus.ACTIVE).pricePerNight(new BigDecimal("3000.00")).cleaningFee(new BigDecimal("300.00"))
                .maxGuests(4).build();
    }

    @Test
    void create_confirmsBookingAndBlocksCalendar() {
        BookingRequest request = new BookingRequest(50L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 4), 2);

        when(userService.getEntityById(2L)).thenReturn(guest);
        when(propertyService.getEntityById(50L)).thenReturn(property);
        when(propertyService.isAvailable(50L, request.checkIn(), request.checkOut())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(500L);
            return b;
        });

        BookingResponse response = bookingService.create(2L, request);

        assertThat(response.id()).isEqualTo(500L);
        assertThat(response.nights()).isEqualTo(3);
        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("9300.00"));
        assertThat(response.status()).isEqualTo("CONFIRMED");
        verify(propertyService).blockForBooking(50L, request.checkIn(), request.checkOut(), 500L);
    }

    @Test
    void create_rejectsWhenDatesUnavailable() {
        BookingRequest request = new BookingRequest(50L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 4), 2);

        when(userService.getEntityById(2L)).thenReturn(guest);
        when(propertyService.getEntityById(50L)).thenReturn(property);
        when(propertyService.isAvailable(50L, request.checkIn(), request.checkOut())).thenReturn(false);

        assertThatThrownBy(() -> bookingService.create(2L, request)).isInstanceOf(BadRequestException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_rejectsHostBookingOwnProperty() {
        BookingRequest request = new BookingRequest(50L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 4), 2);

        when(userService.getEntityById(1L)).thenReturn(host);
        when(propertyService.getEntityById(50L)).thenReturn(property);

        assertThatThrownBy(() -> bookingService.create(1L, request)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void create_rejectsTooManyGuests() {
        BookingRequest request = new BookingRequest(50L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 4), 10);

        when(userService.getEntityById(2L)).thenReturn(guest);
        when(propertyService.getEntityById(50L)).thenReturn(property);

        assertThatThrownBy(() -> bookingService.create(2L, request)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cancel_rejectsUnrelatedUser() {
        Booking booking = Booking.builder().id(500L).property(property).guest(guest)
                .checkIn(LocalDate.of(2026, 3, 1)).checkOut(LocalDate.of(2026, 3, 4))
                .status(BookingStatus.CONFIRMED).build();
        User stranger = User.builder().id(99L).fullName("Stranger").email("stranger@example.com").role(Role.USER).build();

        when(bookingRepository.findById(500L)).thenReturn(java.util.Optional.of(booking));
        when(userService.getEntityById(99L)).thenReturn(stranger);

        assertThatThrownBy(() -> bookingService.cancel(500L, 99L, new CancelBookingRequest("Change of plans")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cancel_releasesCalendarBlockForGuest() {
        Booking booking = Booking.builder().id(500L).property(property).guest(guest)
                .checkIn(LocalDate.of(2026, 3, 1)).checkOut(LocalDate.of(2026, 3, 4))
                .status(BookingStatus.CONFIRMED).build();

        when(bookingRepository.findById(500L)).thenReturn(java.util.Optional.of(booking));
        when(userService.getEntityById(2L)).thenReturn(guest);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.cancel(500L, 2L, new CancelBookingRequest("Change of plans"));

        assertThat(response.status()).isEqualTo("CANCELLED");
        verify(propertyService).releaseBookingBlock(500L);
    }
}
