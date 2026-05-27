package br.edu.streamingplatform.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContentRequest(
        @NotBlank(message = "title is required")
        String title,

        String description,

        @NotBlank(message = "category is required")
        String category,

        @NotBlank(message = "type is required")
        String type,

        @NotNull(message = "durationMinutes is required")
        @Positive(message = "durationMinutes must be greater than zero")
        Integer durationMinutes
) {
}
