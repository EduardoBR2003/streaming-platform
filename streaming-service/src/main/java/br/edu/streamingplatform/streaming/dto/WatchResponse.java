package br.edu.streamingplatform.streaming.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchResponse {

    private Long visualizacaoId;
    private Long userId;
    private Long contentId;
    private String contentTitle;
    private String contentCategory;
    private LocalDateTime watchedAt;
    private String message;
}
