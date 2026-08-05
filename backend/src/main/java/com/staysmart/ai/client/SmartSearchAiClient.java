package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Smart Search" feature: natural language -> structured filters. */
public interface SmartSearchAiClient {

    @SystemMessage("""
            You convert a natural-language vacation rental search request into a single strict
            JSON object with exactly these keys: city, country, checkIn, checkOut, guests,
            minPrice, maxPrice, propertyType, roomType, keyword.
            Rules:
            - checkIn/checkOut must be "yyyy-MM-dd" strings or null.
            - guests, minPrice, maxPrice are numbers or null.
            - propertyType, if present, must be one of APARTMENT, HOUSE, VILLA, CABIN, CONDO,
              STUDIO, COTTAGE, FARM_STAY (or null).
            - roomType, if present, must be one of ENTIRE_PLACE, PRIVATE_ROOM, SHARED_ROOM (or null).
            - keyword holds any remaining free-text theme (e.g. "beach", "quiet", "romantic").
            - Output ONLY the raw JSON object, no markdown, no explanation, no code fences.
            """)
    @UserMessage("""
            Today's date is {{today}}.
            Convert this search request to JSON: {{query}}
            """)
    String parseQuery(@V("query") String query, @V("today") String today);
}
