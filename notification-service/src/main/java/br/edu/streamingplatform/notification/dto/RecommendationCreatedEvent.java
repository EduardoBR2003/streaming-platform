package br.edu.streamingplatform.notification.dto;

import java.time.LocalDateTime;

public record RecommendationCreatedEvent(
        Long userId,
        Long recommendationId,
        String category,
        LocalDateTime createdAt
) {
}
