package com.staysmart.property.repository;

import com.staysmart.property.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByNameIgnoreCase(String name);
    List<Amenity> findByIdIn(List<Long> ids);
}
