package br.edu.streamingplatform.notification.model;

import java.time.LocalDateTime;

import br.edu.streamingplatform.notification.dto.NotificationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recommendation_id")
    private Long recommendationId;

    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_created_at")
    private LocalDateTime eventCreatedAt;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    protected NotificationLog() {
    }

    public NotificationLog(
            Long userId,
            Long recommendationId,
            String category,
            String message,
            String eventType,
            LocalDateTime eventCreatedAt,
            LocalDateTime sentAt
    ) {
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.category = category;
        this.message = message;
        this.eventType = eventType;
        this.eventCreatedAt = eventCreatedAt;
        this.sentAt = sentAt;
    }

    public NotificationDto toDto() {
        return new NotificationDto(id, userId, recommendationId, category, message, eventType, eventCreatedAt, sentAt);
    }
}
