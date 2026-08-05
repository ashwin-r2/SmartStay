package com.staysmart.property.service;

import com.staysmart.exception.ForbiddenException;
import com.staysmart.property.dto.PropertyRequest;
import com.staysmart.property.dto.PropertyResponse;
import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyType;
import com.staysmart.property.entity.RoomType;
import com.staysmart.property.repository.AmenityRepository;
import com.staysmart.property.repository.AvailabilityBlockRepository;
import com.staysmart.property.repository.PropertyRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AvailabilityBlockRepository availabilityBlockRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageStorageService imageStorageService;

    private PropertyService propertyService;

    private User host;

    @BeforeEach
    void setUp() {
        propertyService = new PropertyService(propertyRepository, amenityRepository, availabilityBlockRepository,
                userService, imageStorageService);
        host = User.builder().id(10L).fullName("Host Person").email("host@example.com").role(Role.HOST).build();
    }

    private PropertyRequest sampleRequest() {
        return new PropertyRequest("Cozy Cabin", "A lovely retreat", PropertyType.CABIN, RoomType.ENTIRE_PLACE,
                "1 Forest Rd", "Manali", "Himachal Pradesh", "India", "175131", null, null,
                new BigDecimal("2500.00"), new BigDecimal("200.00"), 4, 2, 2, new BigDecimal("1.5"), List.of());
    }

    @Test
    void create_savesPropertyForHost() {
        when(userService.getEntityById(10L)).thenReturn(host);
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> {
            Property p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        PropertyResponse response = propertyService.create(10L, sampleRequest());

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.title()).isEqualTo("Cozy Cabin");
        assertThat(response.host().id()).isEqualTo(10L);
        assertThat(response.pricePerNight()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    void update_rejectsNonOwnerNonAdmin() {
        Property property = Property.builder().id(100L).host(host).title("Cozy Cabin")
                .propertyType(PropertyType.CABIN).roomType(RoomType.ENTIRE_PLACE)
                .city("Manali").country("India").pricePerNight(new BigDecimal("2500.00")).build();
        User intruder = User.builder().id(99L).fullName("Intruder").email("intruder@example.com").role(Role.USER).build();

        when(propertyRepository.findById(100L)).thenReturn(java.util.Optional.of(property));
        when(userService.getEntityById(99L)).thenReturn(intruder);

        assertThatThrownBy(() -> propertyService.update(100L, 99L, sampleRequest()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void isAvailable_trueWhenNoOverlappingBlocks() {
        when(availabilityBlockRepository.findOverlapping(100L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 15)))
                .thenReturn(List.of());

        boolean available = propertyService.isAvailable(100L, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 15));

        assertThat(available).isTrue();
    }
}
