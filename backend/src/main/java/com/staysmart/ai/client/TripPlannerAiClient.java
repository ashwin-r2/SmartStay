package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Trip Planner" feature. */
public interface TripPlannerAiClient {

    @SystemMessage("""
            You are a knowledgeable travel planner. Produce practical, realistic day-by-day
            itineraries. Do not invent specific business names you are not confident exist;
            prefer describing the type of activity/place instead.
            """)
    @UserMessage("""
            Plan a trip with these details:
            Destination: {{destination}}
            Dates: {{startDate}} to {{endDate}}
            Travelers: {{travelers}}
            Interests: {{interests}}
            Budget level: {{budgetLevel}}

            Respond with a "Day 1", "Day 2", ... breakdown (morning/afternoon/evening suggestions),
            followed by a short "Tips" section with 2-3 practical packing/logistics tips.
            """)
    String planTrip(@V("destination") String destination, @V("startDate") String startDate,
                     @V("endDate") String endDate, @V("travelers") int travelers,
                     @V("interests") String interests, @V("budgetLevel") String budgetLevel);
}
