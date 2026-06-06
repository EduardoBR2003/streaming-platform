package br.edu.streamingplatform.recommendation.model;

import java.time.LocalDateTime;

import br.edu.streamingplatform.recommendation.dto.RecommendationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendations")
public class Recomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "source_content_id", nullable = false)
    private Long sourceContentId;

    @Column(name = "source_content_title", nullable = false)
    private String sourceContentTitle;

    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Recomendacao() {
    }

    public Recomendacao(
            Long userId,
            Long sourceContentId,
            String sourceContentTitle,
            String category,
            String message,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.sourceContentId = sourceContentId;
        this.sourceContentTitle = sourceContentTitle;
        this.category = category;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSourceContentId() {
        return sourceContentId;
    }

    public String getSourceContentTitle() {
        return sourceContentTitle;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public RecommendationDto toDto() {
        return new RecommendationDto(
                id,
                userId,
                sourceContentId,
                sourceContentTitle,
                category,
                message,
                createdAt
        );
    }
}
