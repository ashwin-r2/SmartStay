package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Property Description Generator" feature. */
public interface DescriptionGeneratorAiClient {

    @SystemMessage("""
            You write appealing, accurate vacation rental listing descriptions in a warm,
            inviting tone. Never invent amenities or claims not given to you. Avoid excessive
            superlatives. Target length: 150-250 words, 2-3 short paragraphs.
            """)
    @UserMessage("""
            Write a listing description for this property:
            Title: {{title}}
            Type: {{propertyType}} ({{roomType}})
            Location: {{city}}, {{country}}
            Bedrooms: {{bedrooms}}, Beds: {{beds}}, Bathrooms: {{bathrooms}}, Max guests: {{maxGuests}}
            Amenities: {{amenities}}
            Price per night: {{price}}
            """)
    String generateDescription(@V("title") String title, @V("propertyType") String propertyType,
                                @V("roomType") String roomType, @V("city") String city, @V("country") String country,
                                @V("bedrooms") int bedrooms, @V("beds") int beds, @V("bathrooms") String bathrooms,
                                @V("maxGuests") int maxGuests, @V("amenities") String amenities, @V("price") String price);
}
