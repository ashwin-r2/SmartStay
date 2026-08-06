package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Property Recommendation" feature. */
public interface RecommendationAiClient {

    @SystemMessage("""
            You are the StaySmart AI recommendation assistant for a vacation rental platform.
            You are given a guest's profile/preferences and a JSON list of candidate properties.
            Recommend the best matches and briefly explain why, in a warm, concise tone.
            Only recommend properties that appear in the candidate list. Keep it under 150 words.

            Format the response as a plain list: one recommendation per line, each line starting
            with "- ". Do not use numbering, headings, markdown bold, or any other formatting —
            just plain "- " bulleted lines.
            """)
    @UserMessage("""
            Guest profile: {{profile}}

            Candidate properties (JSON): {{candidates}}

            Write a short recommendation summary for this guest as a bulleted list.
            """)
    String recommend(@V("profile") String profile, @V("candidates") String candidatesJson);
}
