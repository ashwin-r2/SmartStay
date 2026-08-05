package com.staysmart.property.service;

import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.exception.ResourceNotFoundException;
import com.staysmart.property.dto.AvailabilityBlockRequest;
import com.staysmart.property.dto.AvailabilityBlockResponse;
import com.staysmart.property.dto.PropertyRequest;
import com.staysmart.property.dto.PropertyResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.dto.PropertySummaryResponse;
import com.staysmart.property.entity.Amenity;
import com.staysmart.property.entity.AvailabilityBlock;
import com.staysmart.property.entity.AvailabilityReason;
import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyImage;
import com.staysmart.property.entity.PropertyStatus;
import com.staysmart.property.repository.AmenityRepository;
import com.staysmart.property.repository.AvailabilityBlockRepository;
import com.staysmart.property.repository.PropertyRepository;
import com.staysmart.property.specification.PropertySpecifications;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AmenityRepository amenityRepository;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public Property getEntityById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Property", id));
    }

    @Transactional(readOnly = true)
    public PropertyResponse getById(Long id) {
        return PropertyResponse.from(getEntityById(id));
    }

    @Transactional
    public PropertyResponse create(Long hostId, PropertyRequest request) {
        User host = userService.getEntityById(hostId);

        Property property = Property.builder()
                .host(host)
                .title(request.title())
                .description(request.description())
                .propertyType(request.propertyType())
                .roomType(request.roomType())
                .addressLine(request.addressLine())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .pricePerNight(request.pricePerNight())
                .cleaningFee(request.cleaningFee() != null ? request.cleaningFee() : BigDecimal.ZERO)
                .maxGuests(request.maxGuests())
                .bedrooms(request.bedrooms() != null ? request.bedrooms() : 1)
                .beds(request.beds() != null ? request.beds() : 1)
                .bathrooms(request.bathrooms() != null ? request.bathrooms() : BigDecimal.ONE)
                .amenities(resolveAmenities(request.amenityIds()))
                .build();

        Property saved = propertyRepository.save(property);
        log.info("Host {} created property {} ({})", hostId, saved.getId(), saved.getTitle());
        return PropertyResponse.from(saved);
    }

    @Transactional
    public PropertyResponse update(Long propertyId, Long requesterId, PropertyRequest request) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);

        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setPropertyType(request.propertyType());
        property.setRoomType(request.roomType());
        property.setAddressLine(request.addressLine());
        property.setCity(request.city());
        property.setState(request.state());
        property.setCountry(request.country());
        property.setZipCode(request.zipCode());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
        property.setPricePerNight(request.pricePerNight());
        if (request.cleaningFee() != null) property.setCleaningFee(request.cleaningFee());
        property.setMaxGuests(request.maxGuests());
        if (request.bedrooms() != null) property.setBedrooms(request.bedrooms());
        if (request.beds() != null) property.setBeds(request.beds());
        if (request.bathrooms() != null) property.setBathrooms(request.bathrooms());
        property.setAmenities(resolveAmenities(request.amenityIds()));

        Property saved = propertyRepository.save(property);
        log.info("Property {} updated by user {}", propertyId, requesterId);
        return PropertyResponse.from(saved);
    }

    @Transactional
    public void updateDescription(Long propertyId, String description, boolean aiGenerated) {
        Property property = getEntityById(propertyId);
        property.setDescription(description);
        property.setAiGeneratedDescription(aiGenerated);
        propertyRepository.save(property);
    }

    @Transactional
    public PropertyResponse setStatus(Long propertyId, Long requesterId, PropertyStatus status) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);
        property.setStatus(status);
        return PropertyResponse.from(propertyRepository.save(property));
    }

    @Transactional
    public void delete(Long propertyId, Long requesterId) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);
        propertyRepository.delete(property);
        log.info("Property {} deleted by user {}", propertyId, requesterId);
    }

    @Transactional(readOnly = true)
    public Page<PropertySummaryResponse> search(PropertySearchCriteria criteria, Pageable pageable) {
        return propertyRepository.findAll(PropertySpecifications.fromCriteria(criteria), pageable)
                .map(PropertySummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PropertySummaryResponse> listByHost(Long hostId, Pageable pageable) {
        return propertyRepository.findByHostId(hostId, pageable).map(PropertySummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public List<PropertySummaryResponse> listActiveSample(int limit) {
        return propertyRepository.findAll(PropertySpecifications.fromCriteria(PropertySearchCriteria.empty()))
                .stream().limit(limit).map(PropertySummaryResponse::from).toList();
    }

    /** Used by the AI Budget Planner to ground its estimate in real platform pricing for a city. */
    @Transactional(readOnly = true)
    public BigDecimal averagePriceForCity(String city) {
        return propertyRepository.averagePriceForCity(city);
    }

    /** Used by the AI Recommendation feature as a candidate pool for a given city. */
    @Transactional(readOnly = true)
    public List<PropertySummaryResponse> topRatedInCity(String city, int limit) {
        return propertyRepository.findTop20ByCityIgnoreCaseOrderByAvgRatingDesc(city).stream()
                .filter(p -> p.getStatus() == PropertyStatus.ACTIVE)
                .limit(limit)
                .map(PropertySummaryResponse::from)
                .toList();
    }

    @Transactional
    public List<String> uploadImages(Long propertyId, Long requesterId, List<MultipartFile> files) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);

        List<String> urls = imageStorageService.storeAll(files, "properties/" + propertyId);
        int startOrder = property.getImages().size();
        for (int i = 0; i < urls.size(); i++) {
            PropertyImage image = PropertyImage.builder().url(urls.get(i)).displayOrder(startOrder + i).build();
            property.addImage(image);
        }
        propertyRepository.save(property);
        return urls;
    }

    @Transactional
    public void removeImage(Long propertyId, Long imageId, Long requesterId) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);
        boolean removed = property.getImages().removeIf(img -> img.getId().equals(imageId));
        if (!removed) {
            throw ResourceNotFoundException.of("PropertyImage", imageId);
        }
        propertyRepository.save(property);
    }

    // --- Availability -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AvailabilityBlockResponse> getAvailability(Long propertyId) {
        return availabilityBlockRepository.findByPropertyIdOrderByStartDateAsc(propertyId)
                .stream().map(AvailabilityBlockResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(Long propertyId, LocalDate checkIn, LocalDate checkOut) {
        return availabilityBlockRepository.findOverlapping(propertyId, checkIn, checkOut).isEmpty();
    }

    @Transactional
    public AvailabilityBlockResponse blockDates(Long propertyId, Long requesterId, AvailabilityBlockRequest request) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);

        if (!request.endDate().isAfter(request.startDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        if (!isAvailable(propertyId, request.startDate(), request.endDate())) {
            throw new BadRequestException("The selected dates overlap with an existing booking or block");
        }

        AvailabilityBlock block = AvailabilityBlock.builder()
                .property(property)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .reason(AvailabilityReason.BLOCKED)
                .build();
        return AvailabilityBlockResponse.from(availabilityBlockRepository.save(block));
    }

    @Transactional
    public void unblockDates(Long propertyId, Long requesterId, Long blockId) {
        Property property = getEntityById(propertyId);
        assertOwnerOrAdmin(property, requesterId);

        AvailabilityBlock block = availabilityBlockRepository.findById(blockId)
                .orElseThrow(() -> ResourceNotFoundException.of("AvailabilityBlock", blockId));
        if (block.getReason() == AvailabilityReason.BOOKED) {
            throw new BadRequestException("Cannot manually unblock dates tied to an active booking; cancel the booking instead");
        }
        availabilityBlockRepository.delete(block);
    }

    /** Used by the booking module when a booking is confirmed. */
    @Transactional
    public void blockForBooking(Long propertyId, LocalDate checkIn, LocalDate checkOut, Long bookingId) {
        Property property = getEntityById(propertyId);
        AvailabilityBlock block = AvailabilityBlock.builder()
                .property(property)
                .startDate(checkIn)
                .endDate(checkOut)
                .reason(AvailabilityReason.BOOKED)
                .bookingId(bookingId)
                .build();
        availabilityBlockRepository.save(block);
    }

    /** Used by the booking module when a booking is cancelled, to free up the dates again. */
    @Transactional
    public void releaseBookingBlock(Long bookingId) {
        availabilityBlockRepository.deleteByBookingId(bookingId);
    }

    /** Used by the review module after a review is created/updated/deleted. */
    @Transactional
    public void refreshRating(Long propertyId, BigDecimal newAverage, int newCount) {
        Property property = getEntityById(propertyId);
        BigDecimal rounded = newAverage.setScale(2, RoundingMode.HALF_UP);
        property.recalculateRating(rounded, newCount);
        propertyRepository.save(property);
    }

    // --- helpers ------------------------------------------------------------------------

    private Set<Amenity> resolveAmenities(List<Long> amenityIds) {
        if (amenityIds == null || amenityIds.isEmpty()) {
            return Set.of();
        }
        return amenityRepository.findByIdIn(amenityIds).stream().collect(Collectors.toSet());
    }

    private void assertOwnerOrAdmin(Property property, Long requesterId) {
        User requester = userService.getEntityById(requesterId);
        boolean isOwner = property.getHost().getId().equals(requesterId);
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You do not own this property");
        }
    }
}
