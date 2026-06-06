package br.edu.streamingplatform.streaming.service;

import br.edu.streamingplatform.catalog.grpc.proto.ContentResponse;
import br.edu.streamingplatform.streaming.dto.ContentViewedEvent;
import br.edu.streamingplatform.streaming.dto.WatchRequest;
import br.edu.streamingplatform.streaming.dto.WatchResponse;
import br.edu.streamingplatform.streaming.entity.Visualizacao;
import br.edu.streamingplatform.streaming.grpc.CatalogGrpcClient;
import br.edu.streamingplatform.streaming.messaging.RabbitMQPublisher;
import br.edu.streamingplatform.streaming.repository.VisualizacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingService {

    private final VisualizacaoRepository visualizacaoRepository;
    private final CatalogGrpcClient catalogGrpcClient;
    private final RabbitMQPublisher rabbitMQPublisher;

    @Transactional
    public WatchResponse watch(WatchRequest request) {
        log.info("Processing watch request | userId={}, contentId={}", request.getUserId(), request.getContentId());

        ContentResponse content = catalogGrpcClient.getContentById(request.getContentId());

        Visualizacao visualizacao = Visualizacao.builder()
                .userId(request.getUserId())
                .contentId(request.getContentId())
                .contentTitle(content.getTitle())
                .contentCategory(content.getCategory())
                .watchedAt(LocalDateTime.now())
                .build();

        Visualizacao saved = visualizacaoRepository.save(visualizacao);
        log.info("Visualization saved | id={}", saved.getId());

        ContentViewedEvent event = ContentViewedEvent.builder()
                .userId(saved.getUserId())
                .contentId(saved.getContentId())
                .contentTitle(saved.getContentTitle())
                .contentCategory(saved.getContentCategory())
                .viewedAt(saved.getWatchedAt())
                .build();

        rabbitMQPublisher.publishContentViewed(event);

        return WatchResponse.builder()
                .visualizacaoId(saved.getId())
                .userId(saved.getUserId())
                .contentId(saved.getContentId())
                .contentTitle(saved.getContentTitle())
                .contentCategory(saved.getContentCategory())
                .watchedAt(saved.getWatchedAt())
                .message("Watched successfully.")
                .build();
    }

    public List<Visualizacao> getHistoricoByUser(Long userId) {
        return visualizacaoRepository.findByUserIdOrderByWatchedAtDesc(userId);
    }
}
