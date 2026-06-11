package br.edu.streamingplatform.user.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String name,
        String email,
        String plan,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
