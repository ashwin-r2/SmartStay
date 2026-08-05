package com.staysmart.ai.service;

import com.staysmart.ai.client.TripPlannerAiClient;
import com.staysmart.ai.dto.TripPlanRequest;
import com.staysmart.ai.dto.TripPlanResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** AI Trip Planner: generates a day-by-day itinerary for a guest's upcoming trip. */
@Service
@RequiredArgsConstructor
public class TripPlannerAiService {

    private final TripPlannerAiClient client;
    private final AiCallExecutor aiCallExecutor;

    public TripPlanResponse plan(Long userId, TripPlanRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        String interests = (request.interests() == null || request.interests().isBlank())
                ? "general sightseeing" : request.interests();
        String budgetLevel = (request.budgetLevel() == null || request.budgetLevel().isBlank())
                ? "mid-range" : request.budgetLevel();

        String itinerary = aiCallExecutor.call(AiFeature.TRIP_PLANNER, userId, () -> client.planTrip(
                request.destination(), request.startDate().toString(), request.endDate().toString(),
                request.travelers(), interests, budgetLevel));

        return new TripPlanResponse(request.destination(), itinerary);
    }
}
