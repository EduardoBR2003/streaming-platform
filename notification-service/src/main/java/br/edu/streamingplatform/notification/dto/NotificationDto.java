package br.edu.streamingplatform.notification.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        Long userId,
        Long recommendationId,
        String category,
        String message,
        String eventType,
        LocalDateTime eventCreatedAt,
        LocalDateTime sentAt
) {
}
