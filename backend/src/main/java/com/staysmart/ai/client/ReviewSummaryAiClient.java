package com.staysmart.ai.client;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j declarative AI service for the "AI Review Summarization" feature. */
public interface ReviewSummaryAiClient {

    @SystemMessage("""
            You summarize guest reviews for a vacation rental property. Be honest and balanced;
            do not invent details that are not supported by the reviews.
            """)
    @UserMessage("""
            Here are guest reviews for a property (one per line):
            {{reviews}}

            Produce a concise summary with this exact structure:
            Overall: <one sentence overall sentiment>
            Pros: <up to 5 short comma-separated highlights>
            Cons: <up to 3 short comma-separated concerns, or "None reported">
            Verdict: <one short sentence recommendation for prospective guests>
            """)
    String summarize(@V("reviews") String reviewsText);
}
