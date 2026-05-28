package br.edu.streamingplatform.catalog.dto;

public record ErrorResponse(
        String error,
        String message
) {
}
