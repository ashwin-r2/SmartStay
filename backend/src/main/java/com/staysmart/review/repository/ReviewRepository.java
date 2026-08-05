package com.staysmart.review.repository;

import com.staysmart.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByPropertyIdOrderByCreatedAtDesc(Long propertyId, Pageable pageable);

    List<Review> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByBookingId(Long bookingId);

    Optional<Review> findByBookingId(Long bookingId);

    @Query("select coalesce(avg(r.rating), 0) from Review r where r.property.id = :propertyId")
    Double averageRatingForProperty(@Param("propertyId") Long propertyId);

    long countByPropertyId(Long propertyId);
}
