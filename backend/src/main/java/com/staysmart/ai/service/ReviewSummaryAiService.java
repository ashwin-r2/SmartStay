package com.staysmart.ai.service;

import com.staysmart.ai.client.ReviewSummaryAiClient;
import com.staysmart.ai.dto.ReviewSummaryResponse;
import com.staysmart.ai.entity.AiFeature;
import com.staysmart.exception.BadRequestException;
import com.staysmart.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** AI Review Summarization: condenses a property's guest reviews into pros/cons/verdict. */
@Service
@RequiredArgsConstructor
public class ReviewSummaryAiService {

    private static final int MAX_REVIEWS_CONSIDERED = 50;

    private final ReviewSummaryAiClient client;
    private final ReviewService reviewService;
    private final AiCallExecutor aiCallExecutor;

    public ReviewSummaryResponse summarize(Long propertyId, Long userId) {
        List<String> comments = reviewService.getCommentsForProperty(propertyId, MAX_REVIEWS_CONSIDERED);
        if (comments.isEmpty()) {
            throw new BadRequestException("This property has no written reviews yet to summarize");
        }

        String reviewsText = String.join("\n", comments);
        String summary = aiCallExecutor.call(AiFeature.REVIEW_SUMMARY, userId,
                () -> client.summarize(reviewsText));

        return new ReviewSummaryResponse(propertyId, comments.size(), summary);
    }
}
