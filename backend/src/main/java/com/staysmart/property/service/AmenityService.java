package com.staysmart.property.service;

import com.staysmart.exception.ConflictException;
import com.staysmart.exception.ResourceNotFoundException;
import com.staysmart.property.dto.AmenityRequest;
import com.staysmart.property.dto.AmenityResponse;
import com.staysmart.property.entity.Amenity;
import com.staysmart.property.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    @Transactional(readOnly = true)
    public List<AmenityResponse> listAll() {
        return amenityRepository.findAll().stream().map(AmenityResponse::from).toList();
    }

    @Transactional
    public AmenityResponse create(AmenityRequest request) {
        amenityRepository.findByNameIgnoreCase(request.name()).ifPresent(a -> {
            throw new ConflictException("Amenity '" + request.name() + "' already exists");
        });
        Amenity amenity = Amenity.builder().name(request.name()).icon(request.icon()).build();
        return AmenityResponse.from(amenityRepository.save(amenity));
    }

    @Transactional
    public AmenityResponse update(Long id, AmenityRequest request) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Amenity", id));

        amenityRepository.findByNameIgnoreCase(request.name())
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> {
                    throw new ConflictException("Amenity '" + request.name() + "' already exists");
                });

        amenity.setName(request.name());
        amenity.setIcon(request.icon());
        return AmenityResponse.from(amenityRepository.save(amenity));
    }

    @Transactional
    public void delete(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Amenity", id);
        }
        amenityRepository.deleteById(id);
    }
}
