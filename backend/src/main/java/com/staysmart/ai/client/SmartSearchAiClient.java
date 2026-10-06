package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Smart Search" feature: natural language -> structured filters. */
public interface SmartSearchAiClient {

    @SystemMessage("""
            You convert a natural-language vacation rental search request into a single strict
            JSON object with exactly these keys: city, state, country, checkIn, checkOut, guests,
            minPrice, maxPrice, propertyType, roomType, amenities, keyword.
            Rules:
            - city is ONLY an actual city or town (e.g. "Jaipur", "Ooty", "Kochi"). state is a
              state, province, or region (e.g. "Kerala", "Rajasthan", "Tamil Nadu", "Goa",
              "California"). Never put a state name in city. Only set state when the guest names
              one; do not infer it from a city.
            - checkIn/checkOut must be "yyyy-MM-dd" strings or null.
            - guests, minPrice, maxPrice are numbers or null.
            - propertyType, if present, must be one of APARTMENT, HOUSE, VILLA, CABIN, CONDO,
              STUDIO, COTTAGE, FARM_STAY (or null). Only set this when the guest names a SPECIFIC
              type of place. Generic words like "house", "place", "home", "stay", or "property"
              used loosely (e.g. "beach house", "a nice place to stay") do NOT count as a specific
              type - leave propertyType null in that case and let "keyword" carry that language
              instead, so a matching villa/studio/apartment/etc. isn't wrongly filtered out.
            - roomType, if present, must be one of ENTIRE_PLACE, PRIVATE_ROOM, SHARED_ROOM (or null).
            - amenities is a JSON array of amenity names the guest wants, chosen ONLY from this
              exact list (copy the name verbatim, case-sensitive): {{amenityCatalog}}
              If the query doesn't ask for any specific amenity, use an empty array [].
            - keyword holds a concrete feature or theme that would appear in a listing's title or
              description (e.g. "beach", "houseboat", "heritage", "lake view", "desert camp") and
              is not one of the amenities above. It is matched against listing text, so keep it
              short and leave it null rather than include:
              * price words ("cheap", "budget", "affordable", "luxury") - use minPrice/maxPrice;
              * generic words ("stay", "place", "trip", "getaway", "vacation", "holiday", "hotel",
                "rental");
              * words already captured by another field (location, guests, dates, type, amenities);
              * vague moods ("nice", "romantic", "relaxing", "cozy").
            - Output ONLY the raw JSON object, no markdown, no explanation, no code fences.
            """)
    @UserMessage("""
            Today's date is {{today}}.
            Convert this search request to JSON: {{query}}
            """)
    String parseQuery(@V("query") String query, @V("today") String today, @V("amenityCatalog") String amenityCatalog);
}
