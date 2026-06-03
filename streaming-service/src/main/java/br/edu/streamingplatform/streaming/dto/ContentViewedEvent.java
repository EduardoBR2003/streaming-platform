package br.edu.streamingplatform.streaming.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentViewedEvent {

    private Long userId;
    private Long contentId;
    private String contentTitle;
    private String contentCategory;
    private LocalDateTime viewedAt;
}
