package com.staysmart.property.repository;

import com.staysmart.property.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Page<Property> findByHostId(Long hostId, Pageable pageable);

    long countByHostId(Long hostId);

    @Query("select coalesce(avg(p.pricePerNight), 0) from Property p where lower(p.city) = lower(:city)")
    BigDecimal averagePriceForCity(@Param("city") String city);

    List<Property> findTop20ByCityIgnoreCaseOrderByAvgRatingDesc(String city);
}
