package com.staysmart.ai.service;

import com.staysmart.ai.client.TripPlannerAiClient;
import com.staysmart.ai.dto.TripPlanRequest;
import com.staysmart.ai.dto.TripPlanResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.exception.AiServiceException;
import com.staysmart.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripPlannerAiServiceTest {

    @Mock
    private TripPlannerAiClient client;
    @Mock
    private AiCallExecutor aiCallExecutor;

    private TripPlannerAiService service;

    @BeforeEach
    void setUp() {
        service = new TripPlannerAiService(client, aiCallExecutor);
    }

    @SuppressWarnings("unchecked")
    private void stubExecutorToRunSupplier() {
        when(aiCallExecutor.call(eq(AiFeature.TRIP_PLANNER), any(), any(Supplier.class)))
                .thenAnswer(inv -> ((Supplier<String>) inv.getArgument(2)).get());
    }

    @Test
    void plan_callsClientAndReturnsItinerary() {
        stubExecutorToRunSupplier();
        when(client.planTrip("Goa", "2026-03-01", "2026-03-05", 2, "beaches, food", "mid-range"))
                .thenReturn("Day 1: ...");

        TripPlanRequest request = new TripPlanRequest("Goa", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5),
                2, "beaches, food", "mid-range");

        TripPlanResponse response = service.plan(1L, request);

        assertThat(response.destination()).isEqualTo("Goa");
        assertThat(response.itinerary()).isEqualTo("Day 1: ...");
    }

    @Test
    void plan_rejectsInvalidDateRange() {
        TripPlanRequest request = new TripPlanRequest("Goa", LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 1),
                2, null, null);

        assertThatThrownBy(() -> service.plan(1L, request)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void plan_propagatesAiServiceExceptionWhenAiUnavailable() {
        when(aiCallExecutor.call(eq(AiFeature.TRIP_PLANNER), any(), any()))
                .thenThrow(new AiServiceException("AI not configured"));

        TripPlanRequest request = new TripPlanRequest("Goa", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5),
                2, null, null);

        assertThatThrownBy(() -> service.plan(1L, request)).isInstanceOf(AiServiceException.class);
    }
}
