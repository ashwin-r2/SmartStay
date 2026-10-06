package com.staysmart.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staysmart.ai.client.SmartSearchAiClient;
import com.staysmart.ai.dto.SmartSearchResponse;
import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.dto.PropertySummaryResponse;
import com.staysmart.property.repository.AmenityRepository;
import com.staysmart.property.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmartSearchAiServiceTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 20);

    @Mock
    private SmartSearchAiClient client;
    @Mock
    private PropertyService propertyService;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AiCallExecutor aiCallExecutor;

    private SmartSearchAiService service;

    @BeforeEach
    void setUp() {
        service = new SmartSearchAiService(client, propertyService, amenityRepository, aiCallExecutor, new ObjectMapper());
        when(amenityRepository.findAll()).thenReturn(List.of());
    }

    @SuppressWarnings("unchecked")
    private void aiReturns(String json) {
        when(aiCallExecutor.call(any(), any(), any(Supplier.class))).thenReturn(json);
    }

    private static Page<PropertySummaryResponse> pageOf(int total) {
        // PageImpl derives the total from the content size when it all fits on one page.
        List<PropertySummaryResponse> content = Collections.nCopies(total, mock(PropertySummaryResponse.class));
        return new PageImpl<>(content, PAGEABLE, total);
    }

    @Test
    void retriesWithoutKeywordWhenNothingMatches() {
        aiReturns("{\"state\":\"Goa\",\"keyword\":\"romantic\",\"amenities\":[]}");
        when(propertyService.search(any(), eq(PAGEABLE))).thenReturn(pageOf(0), pageOf(2));

        SmartSearchResponse response = service.search(null, "romantic getaway in Goa", PAGEABLE);

        ArgumentCaptor<PropertySearchCriteria> captor = ArgumentCaptor.forClass(PropertySearchCriteria.class);
        verify(propertyService, times(2)).search(captor.capture(), eq(PAGEABLE));
        assertThat(captor.getAllValues().get(0).keyword()).isEqualTo("romantic");
        assertThat(captor.getAllValues().get(1).keyword()).isNull();
        assertThat(captor.getAllValues().get(1).state()).isEqualTo("Goa");
        assertThat(response.interpretedFilters().keyword()).isNull();
        assertThat(response.results().totalElements()).isEqualTo(2);
    }

    @Test
    void keepsKeywordWhenItIsTheOnlyFilter() {
        aiReturns("{\"keyword\":\"treehouse\",\"amenities\":[]}");
        when(propertyService.search(any(), eq(PAGEABLE))).thenReturn(pageOf(0));

        SmartSearchResponse response = service.search(null, "treehouse", PAGEABLE);

        verify(propertyService, times(1)).search(any(), eq(PAGEABLE));
        assertThat(response.interpretedFilters().keyword()).isEqualTo("treehouse");
    }

    @Test
    void keepsKeywordWhenResultsFound() {
        aiReturns("{\"state\":\"Kerala\",\"keyword\":\"houseboat\",\"amenities\":[]}");
        when(propertyService.search(any(), eq(PAGEABLE))).thenReturn(pageOf(2));

        SmartSearchResponse response = service.search(null, "houseboat stay in Kerala", PAGEABLE);

        verify(propertyService, times(1)).search(any(), eq(PAGEABLE));
        assertThat(response.interpretedFilters().keyword()).isEqualTo("houseboat");
    }
}
