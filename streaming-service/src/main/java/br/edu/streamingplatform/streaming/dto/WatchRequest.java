package br.edu.streamingplatform.streaming.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WatchRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "contentId is required")
    private Long contentId;
}
