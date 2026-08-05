package com.staysmart.property.repository;

import com.staysmart.property.entity.AvailabilityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlock, Long> {

    List<AvailabilityBlock> findByPropertyIdOrderByStartDateAsc(Long propertyId);

    @Query("select b from AvailabilityBlock b where b.property.id = :propertyId "
            + "and b.startDate < :endDate and :startDate < b.endDate")
    List<AvailabilityBlock> findOverlapping(@Param("propertyId") Long propertyId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    void deleteByBookingId(Long bookingId);
}
