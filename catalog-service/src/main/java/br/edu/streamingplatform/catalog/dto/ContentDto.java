package br.edu.streamingplatform.catalog.dto;

public record ContentDto(
        Long id,
        String title,
        String description,
        String category,
        String type,
        Integer durationMinutes
) {
}
