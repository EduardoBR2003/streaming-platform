package br.edu.streamingplatform.streaming.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visualizacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "content_title")
    private String contentTitle;

    @Column(name = "content_category")
    private String contentCategory;

    @Column(name = "watched_at", nullable = false)
    private LocalDateTime watchedAt;
}
