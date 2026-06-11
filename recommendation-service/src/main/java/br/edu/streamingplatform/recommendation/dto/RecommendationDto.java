package br.edu.streamingplatform.recommendation.dto;

import java.time.LocalDateTime;

public record RecommendationDto(
        Long id,
        Long userId,
        Long sourceContentId,
        String sourceContentTitle,
        String category,
        String message,
        LocalDateTime createdAt
) {
}
