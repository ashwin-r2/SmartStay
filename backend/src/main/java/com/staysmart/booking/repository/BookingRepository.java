package com.staysmart.booking.repository;

import com.staysmart.booking.entity.Booking;
import com.staysmart.booking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId, Pageable pageable);

    Page<Booking> findByPropertyIdOrderByCreatedAtDesc(Long propertyId, Pageable pageable);

    @Query("select b from Booking b where b.property.host.id = :hostId order by b.createdAt desc")
    Page<Booking> findByPropertyHostId(@Param("hostId") Long hostId, Pageable pageable);

    boolean existsByPropertyIdAndGuestIdAndStatus(Long propertyId, Long guestId, BookingStatus status);

    List<Booking> findByStatusAndCheckOutBefore(BookingStatus status, LocalDate date);

    long countByStatus(BookingStatus status);

    @Query("select coalesce(sum(b.totalPrice), 0) from Booking b where b.status in ('CONFIRMED', 'COMPLETED')")
    BigDecimal totalRevenue();

    @Query("select coalesce(sum(b.totalPrice), 0) from Booking b where b.status in ('CONFIRMED', 'COMPLETED') "
            + "and b.createdAt >= :since")
    BigDecimal revenueSince(@Param("since") java.time.Instant since);

    @Query("select function('date_trunc', 'month', b.createdAt) as month, count(b), coalesce(sum(b.totalPrice), 0) "
            + "from Booking b where b.status in ('CONFIRMED', 'COMPLETED') group by function('date_trunc', 'month', b.createdAt) "
            + "order by month")
    List<Object[]> monthlyBookingStats();

    long countByPropertyHostId(Long hostId);
}
