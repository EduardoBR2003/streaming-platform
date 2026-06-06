package br.edu.streamingplatform.recommendation.dto;

import java.time.LocalDateTime;

public record RecommendationCreatedEvent(
        Long userId,
        Long recommendationId,
        String category,
        LocalDateTime createdAt
) {
}
