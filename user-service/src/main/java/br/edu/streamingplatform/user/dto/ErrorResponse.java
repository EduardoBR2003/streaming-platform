package br.edu.streamingplatform.user.dto;

public record ErrorResponse(
        String error,
        String message
) {
}
