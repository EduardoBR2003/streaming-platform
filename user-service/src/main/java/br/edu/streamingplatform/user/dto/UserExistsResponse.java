package br.edu.streamingplatform.user.dto;

public record UserExistsResponse(
        Long id,
        boolean exists
) {
}
