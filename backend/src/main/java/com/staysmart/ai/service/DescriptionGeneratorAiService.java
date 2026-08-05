package com.staysmart.ai.service;

import com.staysmart.ai.client.DescriptionGeneratorAiClient;
import com.staysmart.ai.dto.DescriptionGenerateRequest;
import com.staysmart.ai.dto.DescriptionGenerateResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.exception.ForbiddenException;
import com.staysmart.property.entity.Property;
import com.staysmart.property.service.PropertyService;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** AI Property Description Generator: drafts a listing description a host can review, tweak, and (optionally) apply directly. */
@Service
@RequiredArgsConstructor
public class DescriptionGeneratorAiService {

    private final DescriptionGeneratorAiClient client;
    private final PropertyService propertyService;
    private final UserService userService;
    private final AiCallExecutor aiCallExecutor;

    public DescriptionGenerateResponse generate(Long userId, DescriptionGenerateRequest request) {
        String amenities = (request.amenities() == null || request.amenities().isEmpty())
                ? "none specified" : String.join(", ", request.amenities());
        String bathrooms = request.bathrooms() != null ? request.bathrooms().toString() : "1";
        String price = request.pricePerNight() != null ? request.pricePerNight().toString() : "not specified";

        String description = aiCallExecutor.call(AiFeature.DESCRIPTION_GENERATOR, userId, () -> client.generateDescription(
                request.title(), request.propertyType().name(), request.roomType().name(),
                request.city(), request.country(), request.bedrooms(), request.beds(), bathrooms,
                request.maxGuests(), amenities, price));

        boolean applied = false;
        if (request.applyToPropertyId() != null) {
            Property property = propertyService.getEntityById(request.applyToPropertyId());
            User requester = userService.getEntityById(userId);
            boolean allowed = property.getHost().getId().equals(userId) || requester.getRole() == Role.ADMIN;
            if (!allowed) {
                throw new ForbiddenException("You do not own this property");
            }
            propertyService.updateDescription(request.applyToPropertyId(), description, true);
            applied = true;
        }

        return new DescriptionGenerateResponse(description, applied);
    }
}
