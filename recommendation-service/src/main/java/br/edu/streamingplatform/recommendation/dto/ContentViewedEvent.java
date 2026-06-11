package br.edu.streamingplatform.recommendation.dto;

import java.time.LocalDateTime;

public record ContentViewedEvent(
        Long userId,
        Long contentId,
        String contentTitle,
        String contentCategory,
        LocalDateTime viewedAt
) {
}
